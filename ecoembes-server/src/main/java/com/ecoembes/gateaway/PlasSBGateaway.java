package com.ecoembes.gateaway;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecoembes.exception.PlantCommunicationException;

import tools.jackson.databind.ObjectMapper; // Spring Boot 3: com.fasterxml.jackson.databind.ObjectMapper

// Salida por API REST hacia el servidor de la planta PlasSB.
public class PlasSBGateaway implements IRecyclingPlantGateaway {

	private static final Logger logger = LoggerFactory.getLogger(PlasSBGateaway.class);

	private static final String URL_PROPERTY = "plassb.api.url";
	private static final String DEFAULT_URL = "http://localhost:8081";

	private final String baseUrl;

	private final HttpClient httpClient;
	private final ObjectMapper objectMapper = new ObjectMapper();

	// El gateway se configura solo: lee su propia URL (ver loadBaseUrl)
	public PlasSBGateaway() {
		this(loadBaseUrl());
	}

	public PlasSBGateaway(String baseUrl) {
		this.baseUrl = baseUrl;
		// Sin connect timeout, la petición se bloquearía si el servidor no está levantado
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(5))
				.build();
	}

	// Orden de prioridad: -Dplassb.api.url=... > application.properties > valor por defecto
	private static String loadBaseUrl() {
		String fromSystem = System.getProperty(URL_PROPERTY);
		if (fromSystem != null && !fromSystem.isBlank()) {
			return fromSystem.trim();
		}

		try (InputStream in = PlasSBGateaway.class.getClassLoader().getResourceAsStream("application.properties")) {
			if (in != null) {
				Properties properties = new Properties();
				properties.load(in);
				String fromFile = properties.getProperty(URL_PROPERTY);
				if (fromFile != null && !fromFile.isBlank()) {
					return fromFile.trim();
				}
			}
		} catch (IOException e) {
			logger.warn("Could not read application.properties: {}", e.getMessage());
		}

		return DEFAULT_URL;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<Integer> getAvailableCapacity(LocalDate date) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(baseUrl + "/capacity?date=" + date))
					.timeout(Duration.ofSeconds(5))
					.GET()
					.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				logger.warn("PlasSB returned HTTP {} when asking for the capacity", response.statusCode());
				return Optional.empty();
			}

			Map<String, Object> body = objectMapper.readValue(response.body(), Map.class);
			Object value = body.get("availableCapacity");

			if (!(value instanceof Number)) {
				return Optional.empty();
			}

			int capacity = ((Number) value).intValue();
			return capacity >= 0 ? Optional.of(capacity) : Optional.empty();

		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return Optional.empty();
		} catch (Exception ex) {
			logger.warn("Could not get the capacity from PlasSB: {}", ex.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public void notifyPlant(LocalDate date, int numDumpsters, int numContainers, List<String> listDumpstersID) {
		try {
			String json = objectMapper.writeValueAsString(Map.of(
					"date", date.toString(),
					"numDumpsters", numDumpsters,
					"numContainers", numContainers,
					"dumpsterIds", listDumpstersID == null ? List.of() : listDumpstersID));

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(baseUrl + "/assignments"))
					.timeout(Duration.ofSeconds(5))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			int status = response.statusCode();

			if (status == 200 || status == 201) {
				return;
			}
			if (status == 409) {
				throw new IllegalStateException("The plant does not have enough capacity");
			}
			throw new PlantCommunicationException("PlasSB rejected the notification (HTTP " + status + ")");

		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new PlantCommunicationException("Interrupted while notifying PlasSB", ex);
		} catch (IOException ex) {
			throw new PlantCommunicationException("Could not notify PlasSB: " + ex.getMessage(), ex);
		}
	}
}