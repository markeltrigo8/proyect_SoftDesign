package com.ecoembes.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

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
		AssigmentDTO result = null;
		
		//Get employee
		Optional<Employee> employee = authService.getEmployeeByToken(token);
		
		List<Integer> dumpsterID = request.getDumpsterIds();
		
		//Calculate total cont
		int totalCont = dumpsterService.calculateTotalCont(dumpsterID);
		
		
		
		return result;
	}
}