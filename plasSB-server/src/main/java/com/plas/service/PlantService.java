package com.plas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.plas.dao.AssignmentRepository;
import com.plas.dao.RecyclingPlantRepository;
import com.plas.domain.Assignment;
import com.plas.domain.RecyclingPlant;

@Service
public class PlantService {

	private final RecyclingPlantRepository plantRepository;
	private final AssignmentRepository assignmentRepository;

	// PlasSB simula una única planta; su id viene de application.properties
	private final int plantId;

	public PlantService(RecyclingPlantRepository plantRepository, AssignmentRepository assignmentRepository,
			@Value("${plassb.plant.id:1}") int plantId) {
		this.plantRepository = plantRepository;
		this.assignmentRepository = assignmentRepository;
		this.plantId = plantId;
	}

	// Capacidad total de la planta menos lo ya asignado ESE día (consulta en BD)
	@Transactional(readOnly = true)
	public int getAvailableCapacity(LocalDate date) {
		if (date == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A date is required");
		}

		RecyclingPlant plant = plantRepository.findById(plantId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found: " + plantId));

		int assigned = assignmentRepository.sumContainersByPlantAndDate(plantId, date);
		return Math.max(0, plant.getCapacity() - assigned);
	}

	// La transacción + el bloqueo de la fila de la planta hacen atómico el
	// "comprobar capacidad y registrar asignación".
	@Transactional
	public Assignment registerAssignment(LocalDate date, int numDumpsters, int numContainers,
			List<String> dumpsterIds) {

		if (date == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A date is required");
		}
		if (dumpsterIds == null || dumpsterIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one dumpster is required");
		}
		if (numDumpsters != dumpsterIds.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"numDumpsters (" + numDumpsters + ") does not match the number of dumpster ids ("
							+ dumpsterIds.size() + ")");
		}
		if (numContainers <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numContainers must be bigger than 0");
		}

		// Planta con bloqueo de escritura (404 si no existe)
		RecyclingPlant plant = plantRepository.findByIdForUpdate(plantId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found: " + plantId));

		int assigned = assignmentRepository.sumContainersByPlantAndDate(plantId, date);
		int available = plant.getCapacity() - assigned;

		if (numContainers > available) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"The plant does not have enough capacity (available: " + Math.max(0, available) + ")");
		}

		return assignmentRepository.save(new Assignment(date, numDumpsters, numContainers, dumpsterIds, plant));
	}
}