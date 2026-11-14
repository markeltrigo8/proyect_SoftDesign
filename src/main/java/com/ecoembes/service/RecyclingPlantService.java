package com.ecoembes.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecoembes.domain.Assigment;
import com.ecoembes.domain.Dumpster;
import com.ecoembes.domain.Employee;
import com.ecoembes.domain.RecyclingPlant;
import com.ecoembes.dto.AssigmentDTO;
import com.ecoembes.dto.AssigmentRequestDTO;
import com.ecoembes.dto.RecyclingPlantDTO;

@Service
public class RecyclingPlantService {

	private Map<Integer, RecyclingPlant> recyclingPlantRepository = new ConcurrentHashMap<>();
	private AuthService authService;
	private DumpsterService dumpsterService;

	public RecyclingPlantService(AuthService authService, DumpsterService dumpsterService) {
	    this.authService = authService;
	    this.dumpsterService = dumpsterService;
	}
	
	public void addRecyclingPlant(RecyclingPlant plant) {
		recyclingPlantRepository.put(plant.getId(), plant);
	}

	public List<RecyclingPlant> getRecyclingPlants() {
		List<RecyclingPlant> result = new ArrayList<>();
		recyclingPlantRepository.forEach((String, RecyclingPlant) ->{
			result.add(RecyclingPlant);
		});
		return result;
	}

	public List<RecyclingPlant> getAllRecyclingPlants() {
		return new ArrayList<>(recyclingPlantRepository.values());
	}
	public Map<RecyclingPlant, Double> getCapacities (String token, LocalDateTime date){
		authenticate(token);
		Map<RecyclingPlant, Double> result = new LinkedHashMap<>();
		
		for (RecyclingPlant r : getAllRecyclingPlants()) {
			result.put(r, getAvailableCapacity(date, r));
		}
		return result;
	}
	public double getAvailableCapacity (LocalDateTime date, RecyclingPlant plant) {
		
		return plant.getTotalCapacity() - plant.getAssignedCapacity(date);
	}
	
	public Assigment assignDumpsterToRecyclingPlant (String token, int plantId, List<Integer> dumpsterIds ) {
		
		Employee employee = authenticate(token);
		
		//Calculate total cont
		int totalCont = dumpsterService.calculateTotalCont(dumpsterIds);
		int totalDump = dumpsterIds.size();
		
		//Get plant
		RecyclingPlant plant = getPlant(plantId);
		
		//Check availability
		if (!IsAvailable(plant.getTotalCapacity(), totalCont)) {
			 throw new IllegalStateException("The plant does not have enough capacity");
        }
		
		Assigment assigment = createAssigment(plant, dumpsterIds, LocalDateTime.now(), totalDump, totalCont, employee);
		
		updateCapacity(plant, totalCont);
	
		return assigment;
	}
	public RecyclingPlant getPlant (int id) {
		RecyclingPlant plant = recyclingPlantRepository.get(id);
		if (plant == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recycling plant not found: " + id);
		}
		return plant;
		
	}
	public boolean IsAvailable (double plantCap, int totalCont) {
		return plantCap >= totalCont;
	}
	private Assigment createAssigment(RecyclingPlant plant, List<Integer> dumpstersID, LocalDateTime date, int totalDump, int totalCont, Employee employee ) {
		List<Dumpster> dumpsters = new ArrayList<Dumpster>();
		for(Integer i : dumpstersID) {
			Dumpster dumpster = dumpsterService.findDumpster(i);
			dumpsters.add(dumpster);
		}
		
		
		Assigment assigment = new Assigment(date, totalDump, totalCont, employee, plant, dumpsters );
		plant.addAssigment(assigment);
		
		return assigment;
	}
	private void updateCapacity(RecyclingPlant plant, double capacityUsed) {
		plant.setCapacity(plant.getTotalCapacity()- capacityUsed);
	}
	

	private Employee authenticate(String token) {
		
		return authService.getEmployeeByToken(token).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token"));
		
	}
}