package com.ecoembes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ecoembes.domain.Dumpster;

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
		return new ArrayList<>(dumpsterRepository.values());
	}
	public int calculateTotalCont(List<Integer> dumpstersID) {
		int result = 0;
		for (int id : dumpstersID) {
			result += dumpsterRepository.get(id).getNumContainers();
		}
		return result;
	}
}