package com.ecoembes.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.ecoembes.exception.PlantCommunicationException;
import com.ecoembes.factory.RecyclingPlantGateawayFactory;
import com.ecoembes.gateaway.IRecyclingPlantGateaway;


@Service
public class RecyclingPlantService {
	
	private static final Logger logger = LoggerFactory.getLogger(RecyclingPlantService.class); // IA
	
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
	public Map<RecyclingPlant, Integer> getCapacities(LocalDate date) {
		Map<RecyclingPlant, Integer> result = new LinkedHashMap<>();
		for (RecyclingPlant r : getAllRecyclingPlants()) {
			getAvailableCapacity(date, r).ifPresentOrElse(
					capacity -> result.put(r, capacity),
					() -> logger.warn("Capacity of plant {} is not available", r.getId()));
		}
		return result;
	}

	@Transactional(readOnly = true)
	public Optional<Integer> getAvailableCapacity(LocalDate date, RecyclingPlant plant) {
		return RecyclingPlantGateawayFactory.createGateway(plant).getAvailableCapacity(date);
	}

	public boolean isAvailable(int availableCap, int totalCont) {
		return availableCap >= totalCont;
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

		if (totalCont <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"The selected dumpsters have no containers to assign");
		}

		LocalDate today = LocalDate.now();

		IRecyclingPlantGateaway gateway = RecyclingPlantGateawayFactory.createGateway(plant);
		int available = gateway.getAvailableCapacity(today)
				.orElseThrow(() -> new PlantCommunicationException(
						"Could not get the capacity of plant " + plantId));

		if (!isAvailable(available, totalCont)) {
			throw new IllegalStateException("The plant does not have enough capacity");
		}

		Assigment assigment = assigmentRepository.save(
				new Assigment(today, totalDump, totalCont, employee, plant, dumpsters));

		gateway.notifyPlant(today, totalDump, totalCont, dumpsterIds);
		return assigment;
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