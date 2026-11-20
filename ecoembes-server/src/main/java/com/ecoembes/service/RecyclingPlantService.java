package com.ecoembes.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecoembes.domain.Assigment;
import com.ecoembes.domain.Dumpster;
import com.ecoembes.domain.Employee;
import com.ecoembes.domain.RecyclingPlant;

@Service
public class RecyclingPlantService {

	private Map<Integer, RecyclingPlant> recyclingPlantRepository = new ConcurrentHashMap<>();
	private DumpsterService dumpsterService;

	public RecyclingPlantService( DumpsterService dumpsterService) {
	    this.dumpsterService = dumpsterService;
	}
	
	public void addRecyclingPlant(RecyclingPlant plant) {
		recyclingPlantRepository.put(plant.getId(), plant);
	}

	public List<RecyclingPlant> getAllRecyclingPlants() {
		List<RecyclingPlant> result = new ArrayList<>(recyclingPlantRepository.values());
		result.sort(Comparator.comparingInt(RecyclingPlant::getId));
		return result;
	}
	
	public Map<RecyclingPlant, Double> getCapacities (LocalDateTime date){
		
		Map<RecyclingPlant, Double> result = new LinkedHashMap<>();
		
		for (RecyclingPlant r : getAllRecyclingPlants()) {
			result.put(r, getAvailableCapacity(date, r));
		}
		return result;
	}
	
	// Capacidad total de la planta menos lo ya asignado ESE dia
	public double getAvailableCapacity (LocalDateTime date, RecyclingPlant plant) {
		
		return plant.getTotalCapacity() - plant.getAssignedCapacity(date);
	}
	
	public Assigment assignDumpsterToRecyclingPlant (Employee employee, int plantId, List<Integer> dumpsterIds ) {
		
		if (dumpsterIds == null || dumpsterIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one dumpster is required");
		}
		if (new HashSet<>(dumpsterIds).size() != dumpsterIds.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicated dumpsters in the request");
		}
		
		//Get plant (404 si no existe)
		RecyclingPlant plant = getPlant(plantId);
		
		//Get dumpsters (404 si alguno no existe)
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
		
		// Comprobar y registrar tienen que ser atomicos: si no, dos peticiones simultaneas
		// podrian pasar la comprobacion a la vez y sobrepasar la capacidad de la planta.
		synchronized (plant) {
			
			//Check availability: capacidad LIBRE de hoy (antes se comparaba con la capacidad total)
			if (!IsAvailable(getAvailableCapacity(now, plant), totalCont)) {
				throw new IllegalStateException("The plant does not have enough capacity");
			}
			
			// Antes ademas se restaba la capacidad de forma permanente (updateCapacity): se descontaba dos veces
			// y para todos los dias. Ahora la capacidad ocupada sale solo de las asignaciones del dia.
			return createAssigment(plant, dumpsters, now, totalDump, totalCont, employee);
		}
	}
	
	public RecyclingPlant getPlant (int id) {
		RecyclingPlant plant = recyclingPlantRepository.get(id);
		if (plant == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recycling plant not found: " + id);
		}
		return plant;
		
	}
	
	public boolean IsAvailable (double availableCap, int totalCont) {
		return availableCap >= totalCont;
	}
	
	private Assigment createAssigment(RecyclingPlant plant, List<Dumpster> dumpsters, LocalDateTime date, int totalDump, int totalCont, Employee employee ) {
		
		Assigment assigment = new Assigment(date, totalDump, totalCont, employee, plant, dumpsters );
		
		// Se registra en los TRES lados de la relacion (antes solo en la planta)
		plant.addAssigment(assigment);
		employee.addAssigment(assigment);
		for (Dumpster d : dumpsters) {
			d.addAssigment(assigment);
		}
		
		return assigment;
	}

}
