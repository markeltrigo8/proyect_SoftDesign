package com.ecoembes.facade;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoembes.dto.DumpsterDTO;
import com.ecoembes.dto.DumpsterStatusDTO;
import com.ecoembes.dto.DumpsterUpdateDTO;
import com.ecoembes.dto.DumpsterUsageDTO;
import com.ecoembes.service.DumpsterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/dumpsters")
@Tag(name = "Dumpsters Controller", description = "Operations related to dumpsters")
public class DumpsterController {

	private DumpsterService dumpsterService;

	public DumpsterController(DumpsterService ds) {
		this.dumpsterService = ds;
	}

	@Operation(
		summary = "Create a new dumpster",
		description = "Returns nothing, but creates a new dumpster in the system.",
		responses = {
			@ApiResponse(responseCode = "200", description = "OK: Dumpster created successfully"),
			@ApiResponse(responseCode = "400", description = "Bad Request: Location and a capacity bigger than 0 are required"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token"),
			@ApiResponse(responseCode = "409", description = "Conflict: The dumpster already exists")
		}
	)
	@PostMapping("/create")
	public ResponseEntity<Void> createDumpster(
			@Parameter(name = "token", description = "Authorization token obtained at login", required = true)
			@RequestParam("token") String token,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dumpster data", required = true)
			@RequestBody DumpsterDTO dumpsterDTO) {

		dumpsterService.createDumpster(token, dumpsterDTO);

		return ResponseEntity.ok().build();
	}

	@Operation(
		summary = "Query dumpster usage",
		description = "Returns the usage of a dumpster between two dates.",
		responses = {
			@ApiResponse(responseCode = "200", description = "OK: Returns the usage of the dumpster"),
			@ApiResponse(responseCode = "400", description = "Bad Request: Invalid start or end date"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token"),
			@ApiResponse(responseCode = "404", description = "Not Found: Dumpster not found")
		}
	)
	@GetMapping("/usage")
	public ResponseEntity<List<DumpsterUsageDTO>> queryDumpsterUsage(
			@Parameter(name = "token", description = "Authorization token obtained at login", required = true)
			@RequestParam("token") String token,
			@Parameter(name = "dumpsterId", description = "ID of the dumpster to query", required = true)
			@RequestParam("dumpsterId") int dumpsterId,
			@Parameter(name = "startDate", description = "Start date, ISO format (yyyy-MM-dd)", required = true, example = "2026-11-01")
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@Parameter(name = "endDate", description = "End date, ISO format (yyyy-MM-dd)", required = true, example = "2026-11-08")
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		List<DumpsterUsageDTO> usage = dumpsterService.queryDumpster(token, dumpsterId, startDate, endDate);

		return ResponseEntity.ok(usage);
	}

	// Cambiado de GET a POST: Swagger UI no permite enviar body en un GET
	@Operation(
		summary = "Check a dumpster status",
		description = "Check the status of the dumpsters of a given postal code and date.",
		responses = {
			@ApiResponse(responseCode = "200", description = "OK: Returns the status of the dumpsters"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token")
		}
	)
	@PostMapping("/status")
	public ResponseEntity<List<DumpsterDTO>> checkDumpsterStatus(
			@Parameter(name = "token", description = "Authorization token obtained at login", required = true)
			@RequestParam("token") String token,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Postal code and date to check", required = true)
			@RequestBody DumpsterStatusDTO dsdto) {

		List<DumpsterDTO> dumpsters = dumpsterService.checkDumpsterStatus(token, dsdto);

		return ResponseEntity.ok(dumpsters);
	}

	@Operation(
		summary = "Update a dumpsters information",
		description = "Transmits daily updates of the dumpsters to the central server.",
		responses = {
			@ApiResponse(responseCode = "200", description = "OK: Dumpster updated successfully"),
			@ApiResponse(responseCode = "400", description = "Bad Request: Invalid fill level"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token"),
			@ApiResponse(responseCode = "404", description = "Not Found: Dumpster not found")
		}
	)
	@PutMapping("/update")
	public ResponseEntity<Void> transmitUpdates(
			@Parameter(name = "token", description = "Authorization token obtained at login", required = true)
			@RequestParam("token") String token,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dumpster update data", required = true)
			@RequestBody DumpsterUpdateDTO dto) {

		dumpsterService.transmitUpdates(dto);

		return ResponseEntity.ok().build();
	}

}