package com.ecoembes.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecoembes.domain.Assigment;
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
	public List<RecyclingPlantDTO> getCapacities (LocalDateTime date){
		List<RecyclingPlantDTO> result = new ArrayList<RecyclingPlantDTO>();
		
		//Get recycling plants
		List<RecyclingPlant> plants = getRecyclingPlants();
		
		//Calcular la capacidad
		for (RecyclingPlant r : plants) {
			double availableCapacity = getAvailableCapacity(date, r);
			
			RecyclingPlantDTO rp = new RecyclingPlantDTO(r.getId(), r.getName(), date.toLocalDate(), availableCapacity);
			
			result.add(rp);
		}
		return result;	
	}
	public double getAvailableCapacity (LocalDateTime date, RecyclingPlant plant) {
		
		return plant.getTotalCapacity() - plant.getAssignedCapacity(date);
	}
	
	public AssigmentDTO assignDumpsterToRecyclingPlant (String token, AssigmentRequestDTO request) {
		
		authenticate(token);
		//Get employee
		Optional<Employee> employee = authService.getEmployeeByToken(token);
		
		List<Integer> dumpsterID = request.getDumpsterIds();
		
		//Calculate total cont
		int totalCont = dumpsterService.calculateTotalCont(dumpsterID);
		int totalDump = dumpsterID.size();
		
		//Get plant
		RecyclingPlant plant = getPlant(request.getRecyclingPlantId());
		
		//Check availability
		if (!IsAvailable(plant.getTotalCapacity(), totalCont)) {
			 throw new IllegalStateException("The plant does not have enough capacity");
        }
		
		Assigment assigment = createAssigment(request.getRecyclingPlantId(), dumpsterID, LocalDateTime.now(), totalDump, totalCont, employee);
		
		updateCapacity(request.getRecyclingPlantId(), totalCont);
	
		return new AssigmentDTO(LocalDate.now(), request.getRecyclingPlantId(), employee.get().getEmail(), dumpsterID, totalCont, totalDump);
	}
	public RecyclingPlant getPlant (int id) {
		return recyclingPlantRepository.get(id);
		
	}
	public boolean IsAvailable (double plantCap, int totalCont) {
		return plantCap >= totalCont;
	}
	private Assigment createAssigment(int plantID, List<Integer> dumpstersID, LocalDateTime date, int totalDump, int totalCont, Optional<Employee> employee ) {
		Assigment assigment = new Assigment(date, totalDump, totalCont);
		
		RecyclingPlant plant = recyclingPlantRepository.get(plantID);
		plant.addAssigment(assigment);
		
		return assigment;
	}
	private void updateCapacity(int plantID, double capacityUsed) {
		RecyclingPlant plant = recyclingPlantRepository.get(plantID);
		plant.setCapacity(plant.getTotalCapacity()- capacityUsed);
	}
	

	private Employee authenticate(String token) {
		
		return authService.getEmployeeByToken(token).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token"));
		
	}
}