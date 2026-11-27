package com.ecoembes.gateaway;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ContSocketGateaway implements IRecyclingPlantGateaway {

	private final String host;
	private final int port;

	public ContSocketGateaway() {
		Properties props = new Properties();
		try (InputStream in = ContSocketGateaway.class.getClassLoader().getResourceAsStream("application.properties")) {
			if (in != null) props.load(in);
		} catch (IOException e) {
			// se usan los valores por defecto
		}
		this.host = System.getProperty("contsocket.host", props.getProperty("contsocket.host", "localhost")).trim();
		this.port = Integer.parseInt(System.getProperty("contsocket.port", props.getProperty("contsocket.port", "9090")).trim());
	}

	@Override
	public Optional<Integer> getAvailableCapacity(LocalDate date) {
		try {
			String[] r = send("GET_CAPACITY;" + date).split(";");
			if (r.length == 2 && r[0].equals("OK")) {
				int cap = Integer.parseInt(r[1]);
				return cap >= 0 ? Optional.of(cap) : Optional.empty();
			}
			return Optional.empty();
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	@Override
	public void notifyPlant(LocalDate date, int numDumpsters, int numContainers, List<String> ids) {
		try {
			String req = "NOTIFY;" + date + ";" + numDumpsters + ";" + numContainers + ";" + String.join(",", ids);
			String[] r = send(req).split(";", 3);
			if (r[0].equals("OK")) return;
			if (r.length >= 2 && r[1].equals("409")) {
				throw new IllegalStateException("The plant does not have enough capacity");
			}
			throw new PlantCommunicationException("ContSocket rejected the notification: " + String.join(";", r));
		} catch (IOException e) {
			throw new PlantCommunicationException("Could not notify ContSocket", e);
		}
	}

	private String send(String request) throws IOException {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, port), 5000);
			socket.setSoTimeout(5000);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(request);
			String response = in.readLine();
			if (response == null) throw new IOException("No response from ContSocket");
			return response;
		}
	}
}