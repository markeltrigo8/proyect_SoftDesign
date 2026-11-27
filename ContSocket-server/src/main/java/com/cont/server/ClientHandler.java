package com.cont.server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cont.domain.Assignment;
import com.cont.exception.RequestException;
import com.cont.service.PlantService;

public class ClientHandler implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

	private final Socket socket;
	private final PlantService plantService;

	public ClientHandler(Socket socket, PlantService plantService) {
		this.socket = socket;
		this.plantService = plantService;
	}

	@Override
	public void run() {
		try (socket;
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
			String line;
			while ((line = in.readLine()) != null) {
				out.println(process(line));
			}
		} catch (IOException e) {
			logger.warn("Connection error: {}", e.getMessage());
		}
	}

	private String process(String line) {
		try {
			String[] p = line.split(";", -1);
			switch (p[0]) {
			case "GET_CAPACITY":
				return "OK;" + plantService.getAvailableCapacity(parseDate(p, 1));
			case "NOTIFY":
				if (p.length != 5) throw new RequestException(400, "NOTIFY needs 4 fields");
				List<String> ids = p[4].isBlank() ? List.of() : Arrays.asList(p[4].split(","));
				Assignment a = plantService.registerAssignment(parseDate(p, 1),
						Integer.parseInt(p[2]), Integer.parseInt(p[3]), ids);
				return "OK;" + a.getId();
			default:
				throw new RequestException(400, "Unknown command: " + p[0]);
			}
		} catch (RequestException e) {
			return "ERROR;" + e.getCode() + ";" + e.getMessage();
		} catch (NumberFormatException e) {
			return "ERROR;400;Invalid number";
		}
	}

	private static LocalDate parseDate(String[] p, int index) {
		if (p.length <= index) throw new RequestException(400, "A date is required");
		try {
			return LocalDate.parse(p[index]);
		} catch (DateTimeParseException e) {
			throw new RequestException(400, "Invalid date (yyyy-MM-dd)");
		}
	}
}