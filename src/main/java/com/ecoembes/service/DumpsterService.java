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
	
	
	public void createDumpster(String token, int id, String location, int postalCode, int capacity) {
		authenticate(token);
		if(location == null || location.isBlank() || capacity <=0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location and a capacity bigger than 0 is required");
			
		}
		
		Dumpster dumpster = new Dumpster(id, location, postalCode, capacity, 0, FillLevel.GREEN);
		
		//IAG funcion hecha con ayudada de la IA
		if(dumpsterRepository.putIfAbsent(dumpster.getId(), dumpster) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The dumpster alreday exists");
		}
		
	}
	
	public List<UsageRecord> queryDumpster(String token, int dumpsterID, LocalDate startDate, LocalDate endDate){
		authenticate(token);
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

	public List<Dumpster> checkDumpsterStatus(String token, int postalCode, LocalDate date){
		authenticate(token);
		List<Dumpster> result = new ArrayList<>();
		for(Dumpster d: dumpsterRepository.values()) {
			if (d.getPostalCode() != postalCode) {
				continue;
			}
			if (findRecordByDay(d, date).isPresent()) {
				result.add(d);
			}
		}
		
		return result;
	}
	
	public void transmitUpdates(int dumpsterId, int numContainers, FillLevel fillLevel) {
		Dumpster dumpster = findDumpster(dumpsterId);
		dumpster.setNumContainers(numContainers);
		dumpster.setFillLevel(fillLevel);
		dumpster.addUsageRecord(new UsageRecord(LocalDateTime.now(), numContainers, fillLevel, dumpster));
	}

	public Dumpster findDumpster(int dumpsterID) {
		return getDumpster(dumpsterID).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, 	"Dumpster not found: "+ dumpsterID));
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

	private Employee authenticate(String token) {
		
		return authservice.getEmployeeByToken(token).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token"));
		
	}
}