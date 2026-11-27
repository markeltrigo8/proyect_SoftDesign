package com.ecoembes.facade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ecoembes.domain.Dumpster;
import com.ecoembes.domain.Employee;
import com.ecoembes.domain.FillLevel;
import com.ecoembes.domain.UsageRecord;
import com.ecoembes.dto.DumpsterDTO;
import com.ecoembes.dto.DumpsterStatusDTO;
import com.ecoembes.dto.DumpsterUpdateDTO;
import com.ecoembes.dto.DumpsterUsageDTO;
import com.ecoembes.service.AuthService;
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
	private AuthService authservice;
	
	public DumpsterController(DumpsterService ds, AuthService authService) {
		this.dumpsterService = ds;
		this.authservice= authService;
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
			@RequestBody DumpsterDTO dto) {
		authenticate(token);
		dumpsterService.createDumpster(token, dto.getId(), dto.getLocation(), dto.getPostalCode(), dto.getCapacity(), dto.getNumContainers());

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
		authenticate(token);
		List<UsageRecord> records = dumpsterService.queryDumpster(token, dumpsterId, startDate, endDate);
		List<DumpsterUsageDTO> usage = records.stream().map(this::createDumpsterUsageDTO).toList();

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
		authenticate(token);
		// Si no llega fecha, el servicio responde 400 (antes: NullPointerException -> 500)
		Map<Dumpster, UsageRecord> found = dumpsterService.checkDumpsterStatus(dsdto.getPostalCode(), dsdto.getDate());
		// El DTO se construye con el registro DE ESE DIA, no con el estado actual del dumpster
		List<DumpsterDTO> dumpsters = new ArrayList<>();
		found.forEach((d, r) -> dumpsters.add(createDumpsterDTO(d, r)));

		return ResponseEntity.ok(dumpsters);
	}

	@Operation(
			summary = "Add a usage record to a dumpster",
			description = "Creates a new usage record (number of containers and fill level) for the given dumpster.",
			responses = {
				@ApiResponse(responseCode = "201", description = "Created: Usage record created successfully"),
				@ApiResponse(responseCode = "400", description = "Bad Request: Invalid fill level"),
				@ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token"),
				@ApiResponse(responseCode = "404", description = "Not Found: Dumpster not found")
			}
		)
		@PostMapping("/update")
		public ResponseEntity<DumpsterUsageDTO> transmitUpdates(
				@Parameter(name = "token", description = "Authorization token obtained at login", required = true)
				@RequestParam("token") String token,
				@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dumpster update data", required = true)
				@RequestBody DumpsterUpdateDTO dto) {
			authenticate(token);
			FillLevel fillLevel = parseFillLevel(dto.getFillLevel());
			UsageRecord record = dumpsterService.transmitUpdates(dto.getDumpsterId(), dto.getNumContainers(), fillLevel);

			return ResponseEntity.status(HttpStatus.CREATED).body(createDumpsterUsageDTO(record));
		}
	// CONVERSION A DTO
	private DumpsterDTO createDumpsterDTO(Dumpster d, UsageRecord r) {
		return new DumpsterDTO(d.getId(), d.getLocation(), d.getPostalCode(), d.getCapacity(),
				r.getNumContainers(), r.getFillLevel());
	}
	private DumpsterUsageDTO createDumpsterUsageDTO(UsageRecord r) {
		return new DumpsterUsageDTO(r.getDate(), r.getNumContainers(), r.getFillLevel().name());
	}

	// Funcion auxiliar creada con IAG
	private FillLevel parseFillLevel(String value) {
		try {
			return FillLevel.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid fill level: " + value);
		}
	}
	
	private Employee authenticate(String token) {
		
		return authservice.getEmployeeByToken(token).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token"));
		
	}
}