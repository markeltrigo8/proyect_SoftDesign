package com.ecoembes.facade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoembes.domain.Assigment;
import com.ecoembes.domain.RecyclingPlant;
import com.ecoembes.dto.AssigmentDTO;
import com.ecoembes.dto.AssigmentRequestDTO;
import com.ecoembes.dto.RecyclingPlantDTO;
import com.ecoembes.service.RecyclingPlantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/recyclingplants")
@Tag(name = "Recycling Plant Controller", description = "Capacity queries and dumpster assignment operations")
public class RecyclingPlantController {

    private final RecyclingPlantService plantService;

    public RecyclingPlantController(RecyclingPlantService plantService) {
        this.plantService = plantService;
    }

    // 1: checkRecyclingPlantCapacity(token, date)
    @Operation(
        summary = "Check recycling plant capacity",
        description = "Returns the capacity of the recycling plants for a given date (format yyyy-MM-dd).",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK: Returns the list of plants with their capacity"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token")
        }
    )
    @GetMapping("/capacity")
    public ResponseEntity<List<RecyclingPlantDTO>> checkRecyclingPlantCapacity(
            @Parameter(name = "token", description = "Authorization token obtained at login", required = true)
            @RequestParam("token") String token,
            @Parameter(name = "date", description = "Date to check, ISO format (yyyy-MM-dd)", required = true, example = "2026-11-08")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    	
    	Map<RecyclingPlant, Double> capacities = plantService.getCapacities(token, date.atStartOfDay());
        // 1.1: getCapacities(date). El service recibe LocalDateTime, así que se convierte.
        List<RecyclingPlantDTO> result = new ArrayList<>();
        capacities.forEach((plant, capacity) -> result.add(createRecyclingPlantDTO(plant, date, capacity)));
        

        // 1.2: List<RecyclingPlantDTO>
        return ResponseEntity.ok(result);
    }

    // 2: assignDumpsterToRecyclingPlant(token, request)
    @Operation(
        summary = "Assign a dumpster to a recycling plant",
        description = "Assigns a dumpster to a recycling plant if the plant has enough capacity.",
        responses = {
            @ApiResponse(responseCode = "200",
                description = "OK: Assignment created, returns the assignment",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                   schema = @Schema(implementation = AssigmentDTO.class))),
            @ApiResponse(responseCode = "400",
                description = "Bad Request: The plant does not have enough capacity",
                content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                                   schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token")
        }
    )
    @PostMapping("/assignments")
    public ResponseEntity<?> assignDumpsterToRecyclingPlant(
            @Parameter(name = "token", description = "Authorization token obtained at login", required = true)
            @RequestParam("token") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Assignment request data", required = true)
            @RequestBody AssigmentRequestDTO request) {

        try {
            
        	Assigment assigment = plantService.assignDumpsterToRecyclingPlant(token, request.getRecyclingPlantId(), request.getDumpsterIds());
        	
        	return ResponseEntity.ok(createAssigmentDTO(assigment, request));
         
        } catch (IllegalStateException e) {
            // 2.2: 400 errorMessage (la planta no tiene capacidad suficiente)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Crear DTO
    private RecyclingPlantDTO createRecyclingPlantDTO(RecyclingPlant plant, LocalDate date, double capacity) {
        return new RecyclingPlantDTO(plant.getId(), plant.getName(), date, capacity);
    }

    private AssigmentDTO createAssigmentDTO(Assigment a, AssigmentRequestDTO request) {
        return new AssigmentDTO(a.getDate().toLocalDate(), request.getRecyclingPlantId(),
                a.getEmployee().getEmail(), request.getDumpsterIds(),
                a.getTotalContainers(), a.getTotalDumpsters());
    }
}