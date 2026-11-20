package com.ecoembes.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecoembes.domain.Dumpster;
import com.ecoembes.domain.FillLevel;
import com.ecoembes.domain.UsageRecord;

@Service
public class DumpsterService {

	private Map<Integer, Dumpster> dumpsterRepository = new ConcurrentHashMap<>();
	
	
	
	public void addDumpster(Dumpster dumpster) {
		dumpsterRepository.put(dumpster.getId(), dumpster);
	}

	public Optional<Dumpster> getDumpster(int id) {
		return Optional.ofNullable(dumpsterRepository.get(id));
	}

	public List<Dumpster> getAllDumpsters() {
		List<Dumpster> result = new ArrayList<>(dumpsterRepository.values());
		result.sort(Comparator.comparingInt(Dumpster::getId));
		return result;
	}

	// Suma los contenedores de los dumpsters indicados. Si alguno no existe -> 404 (antes: NullPointerException -> 500)
	public int calculateTotalCont(List<Integer> dumpstersID) {
		int result = 0;
		for (int id : dumpstersID) {
			result += findDumpster(id).getNumContainers();
		}
		return result;
	}
	
	
	public void createDumpster(String token, int id, String location, int postalCode, int capacity, int numContainers) {
		
		if(location == null || location.isBlank() || capacity <=0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location and a capacity bigger than 0 is required");
			
		}
		
		if (numContainers < 0 || numContainers > capacity) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numContainers must be between 0 and the capacity (" + capacity + ")");
		}
		
		// Antes se ignoraba numContainers y siempre se creaba con 0 y GREEN. El nivel se calcula igual que en DataInitializer.
		FillLevel fillLevel = calculateFillLevel(numContainers, capacity);
		Dumpster dumpster = new Dumpster(id, location, postalCode, capacity, numContainers, fillLevel);
		
		//IAG funcion hecha con ayudada de la IA
		if(dumpsterRepository.putIfAbsent(dumpster.getId(), dumpster) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The dumpster already exists");
		}
		
		// Registro inicial: sin el, el dumpster no aparece en checkDumpsterStatus hasta que llegue una actualizacion
		dumpster.addUsageRecord(new UsageRecord(LocalDateTime.now(), numContainers, fillLevel, dumpster));
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
	
	public List<UsageRecord> queryDumpster(String token, int dumpsterID, LocalDate startDate, LocalDate endDate){
		
		Dumpster dumpster = findDumpster(dumpsterID);
		if(startDate == null || endDate == null || startDate.isAfter(endDate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It is required a valid start and end date");
			
		}
		
		List<UsageRecord> result = new ArrayList<>();
		for (UsageRecord r : dumpster.getUsageHistory()) {
			LocalDate d = r.getDate().toLocalDate();
			if(!d.isBefore(startDate)&&!d.isAfter(endDate)) {
				result.add(r);
			}
		}
		return result;
	}

	// Devuelve cada dumpster del codigo postal junto con su registro de ESE dia.
	// (Antes se devolvia solo el dumpster y el controlador mostraba su estado ACTUAL, no el del dia consultado.)
	public Map<Dumpster, UsageRecord> checkDumpsterStatus(int postalCode, LocalDate date){
	
		if (date == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A date is required");
		}
		
		Map<Dumpster, UsageRecord> result = new LinkedHashMap<>();
		for(Dumpster d: getAllDumpsters()) {
			if (d.getPostalCode() == postalCode) {
				findRecordByDay(d, date).ifPresent(r -> result.put(d, r));
			}
		}
		return result;
	}
	
	public UsageRecord transmitUpdates(int dumpsterId, int numContainers, FillLevel fillLevel) {
		Dumpster dumpster = findDumpster(dumpsterId);
		
		if (fillLevel == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A fill level is required");
		}
		if (numContainers < 0 || numContainers > dumpster.getCapacity()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"numContainers must be between 0 and the dumpster capacity (" + dumpster.getCapacity() + ")");
		}
		
		// Antes: la fecha se forzaba a las 03:00 de hoy (podia quedar en el futuro) y el dumpster no se actualizaba.
		UsageRecord record = new UsageRecord(LocalDateTime.now(), numContainers, fillLevel, dumpster);
		
		synchronized (dumpster) {
			dumpster.addUsageRecord(record);
			// El estado actual del dumpster tiene que reflejar la ultima actualizacion recibida
			dumpster.setNumContainers(numContainers);
			dumpster.setFillLevel(fillLevel);
		}

		return record;
	}

	public Dumpster findDumpster(int dumpsterID) {
		return getDumpster(dumpsterID).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, 	"Dumpster not found: "+ dumpsterID));
	}
	
	
	// Ultimo registro del dia indicado (el historial esta en orden de insercion)
	private Optional<UsageRecord> findRecordByDay(Dumpster d, LocalDate day) {
		UsageRecord found = null;
		for(UsageRecord r : d.getUsageHistory()) {
			if(r.getDate().toLocalDate().equals(day)) {
				found = r;
			}
		}
		return Optional.ofNullable(found);
	}


}
