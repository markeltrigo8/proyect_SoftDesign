package com.cont.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cont.service.PlantService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class SocketServer {

	private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

	private final PlantService plantService;
	private final int port;
	private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
	private ServerSocket serverSocket;

	public SocketServer(PlantService plantService, @Value("${contsocket.server.port:9090}") int port) {
		this.plantService = plantService;
		this.port = port;
	}

	@PostConstruct
	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		logger.info("ContSocket listening on port {}", port);
		Thread acceptor = new Thread(this::acceptLoop, "contsocket-acceptor");
		acceptor.setDaemon(false); // mantiene viva la JVM
		acceptor.start();
	}

	private void acceptLoop() {
		while (!serverSocket.isClosed()) {
			try {
				Socket client = serverSocket.accept();
				executor.submit(new ClientHandler(client, plantService));
			} catch (IOException e) {
				if (!serverSocket.isClosed()) logger.warn("Accept error: {}", e.getMessage());
			}
		}
	}

	@PreDestroy
	public void stop() throws IOException {
		serverSocket.close();
		executor.shutdown();
	}
}