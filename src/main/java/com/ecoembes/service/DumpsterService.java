package com.ecoembes.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.realm.AuthenticatedUserRealm;
import org.aspectj.weaver.patterns.HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecoembes.domain.Dumpster;
import com.ecoembes.domain.Employee;
import com.ecoembes.domain.FillLevel;
import com.ecoembes.domain.UsageRecord;
import com.ecoembes.dto.DumpsterDTO;
import com.ecoembes.dto.DumpsterStatusDTO;
import com.ecoembes.dto.DumpsterUpdateDTO;
import com.ecoembes.dto.DumpsterUsageDTO;

@Service
public class DumpsterService {

	private Map<Integer, Dumpster> dumpsterRepository = new ConcurrentHashMap<>();
	private AuthService authservice;
	
	public DumpsterService(AuthService authService) {
		this.authservice= authService;
	}
	
	
	public void addDumpster(Dumpster dumpster) {
		dumpsterRepository.put(dumpster.getId(), dumpster);
	}

	public Optional<Dumpster> getDumpster(int id) {
		return Optional.ofNullable(dumpsterRepository.get(id));
	}

	public List<Dumpster> getAllDumpsters() {
		return new ArrayList<>(dumpsterRepository.values());
	}
	public int calculateTotalCont(List<Integer> dumpstersID) {
		int result = 0;
		for (int id : dumpstersID) {
			result += dumpsterRepository.get(id).getNumContainers();
		}
		return result;
	}
	
	
	public void createDumpster(String token, DumpsterDTO dto) {
		authenticate(token);
		if(dto == null || dto.getLocation() == null || dto.getLocation().isBlank() || dto.getCapacity() <=0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location and a capacity bigger than 0 is required");
			
		}
		
		Dumpster dumpster = new Dumpster(dto.getId(),dto.getLocation(), dto.getPostalCode(), dto.getCapacity(), 0, FillLevel.GREEN);
		
		//IAG funcion hecha con ayudada de la IA
		if(dumpsterRepository.putIfAbsent(dumpster.getCapacity(), dumpster) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The dumpster alreday exists");
		}
		
	}
	
	public List<DumpsterUsageDTO> queryDumpster(String token, int dumpsterID, LocalDate startDate, LocalDate endDate){
		authenticate(token);
		Dumpster dumpster = findDumpster(dumpsterID);
		if(startDate == null || endDate == null || startDate.isAfter(endDate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It is required a valid start and end date");
			
		}
		
		List<DumpsterUsageDTO> result = new ArrayList<DumpsterUsageDTO>();
		for (UsageRecord r : dumpster.getUsageHistory()) {
			LocalDate d = r.getDate().toLocalDate();
			if(!d.isBefore(startDate)&&!d.isAfter(endDate)) {
				result.add(createDumpsterUsageDTO(r.getDate(), r.getNumContainers(), r.getFillLevel()));
			}
		}
		return result;
	}

	public List<DumpsterDTO> checkDumpsterStatus(String token, DumpsterStatusDTO dumpster){
		authenticate(token);
		LocalDate day = dumpster.getDate().toLocalDate();
		List<DumpsterDTO> result = new ArrayList<DumpsterDTO>();
		
		for (Dumpster d : dumpsterRepository.values()) {
			if(d.getPostalCode() != dumpster.getPostalCode()) {
				continue;
			}
			//Hecho con la ayuda de IAG 
			findRecordByDay(d, day).ifPresent(r ->result.add(createDumpsterDTO(d.getId(), d.getLocation(), d.getPostalCode(),d.getCapacity(), d.getNumContainers(), d.getFillLevel())));
			
		}
		
		return result;
	}
	
	public void transmitUpdates(DumpsterUpdateDTO dto) {
		Dumpster dumpster = findDumpster(dto.getDumpsterId());
		FillLevel fillLevel = parseFillLevel(dto.getFillLevel());
		dumpster.setNumContainers(dto.getNumContainers());
		dumpster.setFillLevel(fillLevel);
		dumpster.addUsageRecord(new UsageRecord(LocalDateTime.now(), dto.getNumContainers(), fillLevel));
	}
	
	//Funcion auxiliar creada con IAG
	private FillLevel parseFillLevel(String value) {
		try {
			return FillLevel.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid fill level: " + value);
		}
	}


	private DumpsterDTO createDumpsterDTO(int id, String location, int postalCode, int capacity, int numContainers,
			FillLevel fillLevel) {
		return new DumpsterDTO(id, location, postalCode, capacity, numContainers, fillLevel);
	}



	private Optional<UsageRecord> findRecordByDay(Dumpster d, LocalDate day) {
		UsageRecord found = null;
		for(UsageRecord r : d.getUsageHistory()) {
			if(r.getDate().toLocalDate().equals(day)) {
				found = r;
			}
		}
		return Optional.ofNullable(found);
	}


	private DumpsterUsageDTO createDumpsterUsageDTO(LocalDateTime date, int numContainers, FillLevel fillLevel) {
		
		return new DumpsterUsageDTO(date, numContainers, fillLevel.name());
	}


	private Dumpster findDumpster(int dumpsterID) {
		return getDumpster(dumpsterID).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, 	"Dumpster not found: "+ dumpsterID));
	}


	private Employee authenticate(String token) {
		
		return authservice.getEmployeeByToken(token).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token"));
		
	}
}