package com.ecoembes.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ecoembes.dao.DumpsterRepository;
import com.ecoembes.dao.UsageRecordRepository;
import com.ecoembes.domain.Dumpster;
import com.ecoembes.domain.FillLevel;
import com.ecoembes.domain.UsageRecord;

@Service
public class DumpsterService {

	private final DumpsterRepository dumpsterRepository;
	private final UsageRecordRepository usageRecordRepository;

	public DumpsterService(DumpsterRepository dumpsterRepository, UsageRecordRepository usageRecordRepository) {
		this.dumpsterRepository = dumpsterRepository;
		this.usageRecordRepository = usageRecordRepository;
	}

	// Guarda también los UsageRecord que ya tenga añadidos (cascade ALL)
	@Transactional
	public void addDumpster(Dumpster dumpster) {
		dumpsterRepository.save(dumpster);
	}

	@Transactional(readOnly = true)
	public Optional<Dumpster> getDumpster(int id) {
		return dumpsterRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public List<Dumpster> getAllDumpsters() {
		return dumpsterRepository.findAll(Sort.by("id"));
	}

	// Suma los contenedores de los dumpsters indicados. Si alguno no existe -> 404
	@Transactional(readOnly = true)
	public int calculateTotalCont(List<Integer> dumpstersID) {
		int result = 0;
		for (int id : dumpstersID) {
			result += findDumpster(id).getNumContainers();
		}
		return result;
	}

	@Transactional
	public void createDumpster(String token, int id, String location, int postalCode, int capacity, int numContainers) {

		if (location == null || location.isBlank() || capacity <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location and a capacity bigger than 0 is required");
		}

		if (numContainers < 0 || numContainers > capacity) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"numContainers must be between 0 and the capacity (" + capacity + ")");
		}

		// Con id asignado a mano, save() haría "merge" y sobrescribiría uno existente,
		// por eso hay que comprobar antes que no exista.
		if (dumpsterRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The dumpster already exists");
		}

		FillLevel fillLevel = calculateFillLevel(numContainers, capacity);
		Dumpster dumpster = dumpsterRepository.save(
				new Dumpster(id, location, postalCode, capacity, numContainers, fillLevel));

		// Registro inicial: sin él, el dumpster no aparece en checkDumpsterStatus hasta que llegue una actualización
		usageRecordRepository.save(new UsageRecord(LocalDateTime.now(), numContainers, fillLevel, dumpster));
	}

	private static FillLevel calculateFillLevel(int numContainers, int capacity) {
		double ratio = (double) numContainers / capacity;
		if (ratio < 0.5) {
			return FillLevel.GREEN;
		} else if (ratio < 0.8) {
			return FillLevel.ORANGE;
		}
		return FillLevel.RED;
	}

	@Transactional(readOnly = true)
	public List<UsageRecord> queryDumpster(String token, int dumpsterID, LocalDate startDate, LocalDate endDate) {

		findDumpster(dumpsterID); // 404 si no existe
		if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It is required a valid start and end date");
		}

		// La BD filtra por rango de fechas (inicio del primer día hasta el final del último)
		return usageRecordRepository.findByDumpsterIdAndDateBetweenOrderByDateAscIdAsc(
				dumpsterID, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
	}

	// Devuelve cada dumpster del código postal junto con su registro de ESE día.
	@Transactional(readOnly = true)
	public Map<Dumpster, UsageRecord> checkDumpsterStatus(int postalCode, LocalDate date) {

		if (date == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A date is required");
		}

		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.atTime(LocalTime.MAX);

		Map<Dumpster, UsageRecord> result = new LinkedHashMap<>();
		List<Dumpster> dumpsters = dumpsterRepository.findByPostalCode(postalCode);
		dumpsters.sort(Comparator.comparingInt(Dumpster::getId));

		for (Dumpster d : dumpsters) {
			usageRecordRepository.findFirstByDumpsterIdAndDateBetweenOrderByDateDescIdDesc(d.getId(), start, end)
					.ifPresent(r -> result.put(d, r));
		}
		return result;
	}

	// El bloqueo de la fila del dumpster sustituye al synchronized
	@Transactional
	public UsageRecord transmitUpdates(int dumpsterId, int numContainers, FillLevel fillLevel) {
		Dumpster dumpster = dumpsterRepository.findByIdForUpdate(dumpsterId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Dumpster not found: " + dumpsterId));

		if (fillLevel == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A fill level is required");
		}
		if (numContainers < 0 || numContainers > dumpster.getCapacity()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"numContainers must be between 0 and the dumpster capacity (" + dumpster.getCapacity() + ")");
		}

		UsageRecord record = usageRecordRepository.save(
				new UsageRecord(LocalDateTime.now(), numContainers, fillLevel, dumpster));

		// El estado actual refleja la última actualización (se guarda solo al terminar la transacción)
		dumpster.setNumContainers(numContainers);
		dumpster.setFillLevel(fillLevel);

		return record;
	}

	@Transactional(readOnly = true)
	public Dumpster findDumpster(int dumpsterID) {
		return getDumpster(dumpsterID).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dumpster not found: " + dumpsterID));
	}

}