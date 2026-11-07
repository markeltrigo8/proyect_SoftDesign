package com.ecoembes.facade;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import com.ecoembes.dto.AssigmentDTO;
import com.ecoembes.dto.AssigmentRequestDTO;
import com.ecoembes.dto.RecyclingPlantDTO;
import com.ecoembes.service.RecyclingPlantService;

// CÓDIGO GENERADO CON INTELIGENCIA ARTIFICAL GENERATIVA

@RestController
@RequestMapping("/recyclingplants")
public class RecyclingPlantController {

    private final RecyclingPlantService plantService;

    public RecyclingPlantController(RecyclingPlantService plantService) {
        this.plantService = plantService;
    }

    // 1: checkRecyclingPlantCapacity(token, date)
    @GetMapping("/capacity")
    public ResponseEntity<List<RecyclingPlantDTO>> checkRecyclingPlantCapacity(
            @RequestHeader("Authorization") String token,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // 1.1: getCapacities(date). El service recibe LocalDateTime, así que se convierte.
        List<RecyclingPlantDTO> capacities = plantService.getCapacities(date.atStartOfDay());

        // 1.2: List<RecyclingPlantDTO>
        return ResponseEntity.ok(capacities);
    }

    // 2: assignDumpsterToRecyclingPlant(token, request)
    @PostMapping("/assignments")
    public ResponseEntity<?> assignDumpsterToRecyclingPlant(
            @RequestHeader("Authorization") String token,
            @RequestBody AssigmentRequestDTO request) {

        try {
            // 2.1: assignDumpsterToRecyclingPlant(token, request)
            AssigmentDTO assigmentDTO = plantService.assignDumpsterToRecyclingPlant(token, request);

            // 2.1.11.1: assigmentDTO
            return ResponseEntity.ok(assigmentDTO);

        } catch (IllegalStateException e) {
            // 2.2: 400 errorMessage (la planta no tiene capacidad suficiente)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}