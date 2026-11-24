package com.ecoembes.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ecoembes.dao.AssigmentRepository;
import com.ecoembes.dao.RecyclingPlantRepository;
import com.ecoembes.domain.Assigment;
import com.ecoembes.domain.Dumpster;
import com.ecoembes.domain.Employee;
import com.ecoembes.domain.RecyclingPlant;

@Service
public class RecyclingPlantService {

	private final RecyclingPlantRepository recyclingPlantRepository;
	private final AssigmentRepository assigmentRepository;
	private final DumpsterService dumpsterService;

	public RecyclingPlantService(RecyclingPlantRepository recyclingPlantRepository,
			AssigmentRepository assigmentRepository, DumpsterService dumpsterService) {
		this.recyclingPlantRepository = recyclingPlantRepository;
		this.assigmentRepository = assigmentRepository;
		this.dumpsterService = dumpsterService;
	}

	@Transactional
	public void addRecyclingPlant(RecyclingPlant plant) {
		recyclingPlantRepository.save(plant);
	}

	@Transactional(readOnly = true)
	public List<RecyclingPlant> getAllRecyclingPlants() {
		return recyclingPlantRepository.findAll(Sort.by("id"));
	}

	@Transactional(readOnly = true)
	public Map<RecyclingPlant, Double> getCapacities(LocalDateTime date) {
		Map<RecyclingPlant, Double> result = new LinkedHashMap<>();
		for (RecyclingPlant r : getAllRecyclingPlants()) {
			result.put(r, getAvailableCapacity(date, r));
		}
		return result;
	}

	// Capacidad total de la planta menos lo ya asignado ESE día (consulta en BD)
	@Transactional(readOnly = true)
	public double getAvailableCapacity(LocalDateTime date, RecyclingPlant plant) {
		LocalDateTime start = date.toLocalDate().atStartOfDay();
		LocalDateTime end = start.plusDays(1);
		int assigned = assigmentRepository.sumContainersByPlantAndDay(plant.getId(), start, end);
		return plant.getTotalCapacity() - assigned;
	}

	// La transacción + el bloqueo de la fila de la planta hacen atómico el
	// "comprobar capacidad y registrar asignación" (sustituye al synchronized).
	@Transactional
	public Assigment assignDumpsterToRecyclingPlant(Employee employee, int plantId, List<Integer> dumpsterIds) {

		if (dumpsterIds == null || dumpsterIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one dumpster is required");
		}
		if (new HashSet<>(dumpsterIds).size() != dumpsterIds.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicated dumpsters in the request");
		}

		// Planta con bloqueo de escritura (404 si no existe)
		RecyclingPlant plant = recyclingPlantRepository.findByIdForUpdate(plantId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Recycling plant not found: " + plantId));

		// Contenedores (404 si alguno no existe)
		List<Dumpster> dumpsters = new ArrayList<>();
		for (Integer id : dumpsterIds) {
			dumpsters.add(dumpsterService.findDumpster(id));
		}

		int totalDump = dumpsters.size();
		int totalCont = 0;
		for (Dumpster d : dumpsters) {
			totalCont += d.getNumContainers();
		}

		LocalDateTime now = LocalDateTime.now();

		if (!isAvailable(getAvailableCapacity(now, plant), totalCont)) {
			throw new IllegalStateException("The plant does not have enough capacity");
		}

		// Basta con guardar la Assigment: es el lado que tiene las claves foráneas
		// (employee, planta y tabla intermedia con los dumpsters).
		return assigmentRepository.save(new Assigment(now, totalDump, totalCont, employee, plant, dumpsters));
	}

	@Transactional(readOnly = true)
	public RecyclingPlant getPlant(int id) {
		return recyclingPlantRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Recycling plant not found: " + id));
	}

	public boolean isAvailable(double availableCap, int totalCont) {
		return availableCap >= totalCont;
	}
}