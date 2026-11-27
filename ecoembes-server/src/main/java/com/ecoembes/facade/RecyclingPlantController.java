package com.ecoembes.facade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ecoembes.domain.Assigment;
import com.ecoembes.domain.Employee;
import com.ecoembes.domain.RecyclingPlant;
import com.ecoembes.dto.AssigmentDTO;
import com.ecoembes.dto.AssigmentRequestDTO;
import com.ecoembes.dto.RecyclingPlantDTO;
import com.ecoembes.service.AuthService;
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
	private AuthService authService;

    public RecyclingPlantController(AuthService authService,RecyclingPlantService plantService) {
        this.plantService = plantService;
	    this.authService = authService;
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

    	authenticate(token);
    	Map<RecyclingPlant, Integer> capacities = plantService.getCapacities(date);
        // 1.1: getCapacities(date). El service recibe LocalDateTime, así que se convierte.
        List<RecyclingPlantDTO> result = new ArrayList<>();
        capacities.forEach((plant, capacity) -> result.add(createRecyclingPlantDTO(plant, date, capacity)));
        

        // 1.2: List<RecyclingPlantDTO>
        return ResponseEntity.ok(result);
    }

    // 2: assignDumpsterToRecyclingPlant(token, request)
    @Operation(
    		summary = "Assign dumpsters to a recycling plant",
    		description = "Assigns dumpsters to a recycling plant if it has enough capacity (in containers) and notifies the plant.",
    		responses = {
    			@ApiResponse(responseCode = "200", description = "OK: Assignment created, returns the assignment"),
    			@ApiResponse(responseCode = "400", description = "Bad Request: No dumpsters, duplicated dumpsters or no containers to assign"),
    			@ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token"),
    			@ApiResponse(responseCode = "404", description = "Not Found: Plant or dumpster not found"),
    			@ApiResponse(responseCode = "409", description = "Conflict: The plant does not have enough capacity"),
    			@ApiResponse(responseCode = "502", description = "Bad Gateway: The recycling plant server did not respond")
    		}
    	)
    	@PostMapping("/assignments")
    	public ResponseEntity<AssigmentDTO> assignDumpsterToRecyclingPlant(
    			@Parameter(name = "token", description = "Authorization token obtained at login", required = true)
    			@RequestParam("token") String token,
    			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Assignment request data", required = true)
    			@RequestBody AssigmentRequestDTO request) {

    		Employee employee = authenticate(token);
    		Assigment assigment = plantService.assignDumpsterToRecyclingPlant(employee,
    				request.getRecyclingPlantId(), request.getDumpsterIds());

    		return ResponseEntity.ok(createAssigmentDTO(assigment, request));
    	}
    
    // Crear DTO
    private RecyclingPlantDTO createRecyclingPlantDTO(RecyclingPlant plant, LocalDate date, int capacity) {
        return new RecyclingPlantDTO(plant.getId(), plant.getName(), date, capacity);
    }

    private AssigmentDTO createAssigmentDTO(Assigment a, AssigmentRequestDTO request) {
        return new AssigmentDTO(a.getDate(), request.getRecyclingPlantId(),
                a.getEmployee().getEmail(), request.getDumpsterIds(),
                a.getTotalContainers(), a.getTotalDumpsters());
    }
    
	private Employee authenticate(String token) {
		
		return authService.getEmployeeByToken(token).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token"));
		
	}
}