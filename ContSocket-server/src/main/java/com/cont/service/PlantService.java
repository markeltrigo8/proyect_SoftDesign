package com.cont.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cont.dao.AssignmentRepository;
import com.cont.domain.Assignment;
import com.cont.exception.RequestException;

@Service
public class PlantService {

	private final AssignmentRepository repository;
	private final int capacity;

	public PlantService(AssignmentRepository repository, @Value("${contsocket.plant.capacity:85}") int capacity) {
		this.repository = repository;
		this.capacity = capacity;
	}

	// synchronized sustituye al bloqueo de fila de la BD: "comprobar + guardar" es atómico
	public synchronized int getAvailableCapacity(LocalDate date) {
		if (date == null) throw new RequestException(400, "A date is required");
		return Math.max(0, capacity - repository.sumContainersByDate(date));
	}

	public synchronized Assignment registerAssignment(LocalDate date, int numDumpsters, int numContainers,
			List<String> dumpsterIds) {
		if (date == null) throw new RequestException(400, "A date is required");
		if (dumpsterIds == null || dumpsterIds.isEmpty()) throw new RequestException(400, "At least one dumpster is required");
		if (numDumpsters != dumpsterIds.size()) throw new RequestException(400, "numDumpsters does not match the number of ids");
		if (numContainers <= 0) throw new RequestException(400, "numContainers must be bigger than 0");

		int available = capacity - repository.sumContainersByDate(date);
		if (numContainers > available) {
			throw new RequestException(409, "The plant does not have enough capacity");
		}
		return repository.save(date, numDumpsters, numContainers, dumpsterIds);
	}
}