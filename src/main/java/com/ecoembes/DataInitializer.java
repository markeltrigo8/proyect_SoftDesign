//Clase creada con IAG para la prueba del programa con datos de prueba. No es parte del proyecto original.

package com.ecoembes;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecoembes.domain.Dumpster;
import com.ecoembes.domain.Employee;
import com.ecoembes.domain.FillLevel;
import com.ecoembes.domain.RecyclingPlant;
import com.ecoembes.domain.UsageRecord;
import com.ecoembes.service.AuthService;
import com.ecoembes.service.DumpsterService;
import com.ecoembes.service.RecyclingPlantService;

@Configuration
public class DataInitializer {

	private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

	private static final int USAGE_HISTORY_DAYS = 5;

	@Bean
	CommandLineRunner initData(AuthService authService,
			DumpsterService dumpsterService,
			RecyclingPlantService recyclingPlantService) {
		return args -> {

			// ---------- Empleados ----------
			Employee ane = new Employee("Ane Etxebarria", "ane.etxebarria@ecoembes.com", "An3Etx#2026");
			Employee jon = new Employee("Jon Aguirre", "jon.aguirre@ecoembes.com", "J0nAgu!rre26");
			Employee maite = new Employee("Maite Urkiza", "maite.urkiza@ecoembes.com", "M@iteUrk1za");

			authService.addEmployee(ane);
			authService.addEmployee(jon);
			authService.addEmployee(maite);

			logger.info("Employees saved!");

			// ---------- Plantas de reciclaje ----------
			RecyclingPlant plant1 = new RecyclingPlant("PLANT-001", "Ecoplanta Bilbao Norte", 120.0);
			RecyclingPlant plant2 = new RecyclingPlant("PLANT-002", "Ecoplanta Ezkerraldea", 85.5);
			RecyclingPlant plant3 = new RecyclingPlant("PLANT-003", "Ecoplanta Uribe Kosta", 60.0);

			recyclingPlantService.addRecyclingPlant(plant1);
			recyclingPlantService.addRecyclingPlant(plant2);
			recyclingPlantService.addRecyclingPlant(plant3);

			logger.info("Recycling plants saved!");

			// ---------- Contenedores (dumpsters) ----------
			Dumpster d1 = createDumpster(1, "Gran Vía 45, Bilbao", 48011, 20, 5);
			Dumpster d2 = createDumpster(2, "Calle Autonomía 12, Bilbao", 48010, 20, 12);
			Dumpster d3 = createDumpster(3, "Avenida Lehendakari Aguirre 30, Bilbao", 48014, 25, 22);
			Dumpster d4 = createDumpster(4, "Plaza Zabalburu 3, Bilbao", 48003, 18, 10);
			Dumpster d5 = createDumpster(5, "Herriko Plaza 8, Barakaldo", 48901, 15, 4);
			Dumpster d6 = createDumpster(6, "Avenida Libertad 22, Barakaldo", 48902, 15, 13);
			Dumpster d7 = createDumpster(7, "Calle Algorta 5, Getxo", 48992, 30, 16);
			Dumpster d8 = createDumpster(8, "Calle Andra Mari 17, Getxo", 48993, 30, 28);
			Dumpster d9 = createDumpster(9, "Calle Nagusia 9, Portugalete", 48920, 20, 9);
			Dumpster d10 = createDumpster(10, "Avenida Basauri 40, Basauri", 48970, 20, 19);

			Dumpster[] dumpsters = { d1, d2, d3, d4, d5, d6, d7, d8, d9, d10 };

			for (Dumpster d : dumpsters) {
				dumpsterService.addDumpster(d);
			}

			logger.info("Dumpsters saved!");

			// ---------- Historial de uso ----------
			for (Dumpster d : dumpsters) {
				addUsageHistory(d, USAGE_HISTORY_DAYS);
			}

			logger.info("Usage records saved!");
		};
	}

	
	private static Dumpster createDumpster(int id, String location, int postalCode, int capacity, int numContainers) {
		return new Dumpster(id, location, postalCode, capacity, numContainers,
				calculateFillLevel(numContainers, capacity));
	}


	private static FillLevel calculateFillLevel(int numContainers, int capacity) {
		double ratio = (double) numContainers / capacity;
		if (ratio < 0.5) {
			return FillLevel.GREEN;
		} else if (ratio < 0.8) {
			return FillLevel.ORANGE;
		} else {
			return FillLevel.RED;
		}
	}


	private static void addUsageHistory(Dumpster dumpster, int days) {
		LocalDateTime now = LocalDateTime.now();

		for (int i = 0; i < days; i++) {
			int daysAgo = days - 1 - i;
			int numContainers = dumpster.getNumContainers() * (i + 1) / days;

			UsageRecord record = new UsageRecord(
					now.minusDays(daysAgo),
					numContainers,
					calculateFillLevel(numContainers, dumpster.getCapacity()));

			dumpster.addUsageRecord(record);
		}
	}
}