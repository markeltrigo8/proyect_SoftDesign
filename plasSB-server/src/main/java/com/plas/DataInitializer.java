//Clase creada con IAG para la prueba del programa con datos de prueba. No es parte del proyecto original.

package com.plas;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.plas.dao.AssignmentRepository;
import com.plas.dao.RecyclingPlantRepository;
import com.plas.domain.Assignment;
import com.plas.domain.RecyclingPlant;

@Configuration
public class DataInitializer {

	private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

	@SuppressWarnings("unused")
	@Bean
	CommandLineRunner initData(RecyclingPlantRepository plantRepository,
			AssignmentRepository assignmentRepository,
			@Value("${plassb.plant.id:1}") int plantId,
			@Value("${plassb.plant.name:PlasSB Ecoplanta}") String plantName,
			@Value("${plassb.plant.capacity:85}") int plantCapacity) {
		return args -> {

			// La base de datos ya está inicializada (evita duplicar datos en cada arranque
			// si la BD es persistente)
			if (plantRepository.count() > 0) {
				logger.info("Database already initialized, skipping test data.");
				return;
			}

			// ---------- Planta ----------
			// save() devuelve la instancia gestionada: es la que se usa en las asignaciones
			RecyclingPlant plant = plantRepository.save(new RecyclingPlant(plantId, plantName, plantCapacity));

			logger.info("Plant saved!");

			// ---------- Asignaciones de ejemplo ----------
			// Para que GET /capacity ya muestre una capacidad reducida en la demo.
			// (Los ids de dumpster coinciden con los del DataInitializer de Ecoembes.)
			LocalDate today = LocalDate.now();

			assignmentRepository.save(new Assignment(today, 3, 12, List.of("1", "2", "3"), plant));
			assignmentRepository.save(new Assignment(today.minusDays(1), 2, 20, List.of("7", "8"), plant));

			logger.info("Assignments saved!");
		};
	}
}