package com.plas.facade;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.plas.dto.AssignmentDTO;
import com.plas.dto.AssignmentRequestDTO;
import com.plas.dto.CapacityDTO;
import com.plas.domain.Assignment;
import com.plas.service.PlantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "PlasSB Plant Controller", description = "Capacity query and assignment registration of the PlasSB plant")
public class PlantController {

	private final PlantService plantService;

	public PlantController(PlantService plantService) {
		this.plantService = plantService;
	}

	@Operation(
		summary = "Get the available capacity",
		description = "Returns the free capacity (in containers) of the plant for a given date (format yyyy-MM-dd).",
		responses = {
			@ApiResponse(responseCode = "200", description = "OK: Returns the available capacity"),
			@ApiResponse(responseCode = "400", description = "Bad Request: Missing or invalid date"),
			@ApiResponse(responseCode = "404", description = "Not Found: The plant does not exist")
		}
	)
	@GetMapping("/capacity")
	public ResponseEntity<CapacityDTO> getAvailableCapacity(
			@Parameter(name = "date", description = "Date to check, ISO format (yyyy-MM-dd)", required = true, example = "2026-11-26")
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		return ResponseEntity.ok(new CapacityDTO(plantService.getAvailableCapacity(date)));
	}

	@Operation(
		summary = "Register an assignment",
		description = "Registers the dumpsters that Ecoembes assigns to the plant, if there is enough capacity that day.",
		responses = {
			@ApiResponse(responseCode = "201", description = "Created: Assignment registered"),
			@ApiResponse(responseCode = "400", description = "Bad Request: Invalid assignment data"),
			@ApiResponse(responseCode = "404", description = "Not Found: The plant does not exist"),
			@ApiResponse(responseCode = "409", description = "Conflict: The plant does not have enough capacity")
		}
	)
	@PostMapping("/assignments")
	public ResponseEntity<AssignmentDTO> registerAssignment(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Assignment data", required = true)
			@RequestBody AssignmentRequestDTO request) {

		Assignment assignment = plantService.registerAssignment(request.getDate(), request.getNumDumpsters(),
				request.getNumContainers(), request.getDumpsterIds());

		return ResponseEntity.status(HttpStatus.CREATED).body(createAssignmentDTO(assignment));
	}

	// CONVERSION A DTO
	private AssignmentDTO createAssignmentDTO(Assignment a) {
		return new AssignmentDTO(a.getId(), a.getDate(), a.getNumDumpsters(), a.getNumContainers(),
				a.getDumpsterIds());
	}
}