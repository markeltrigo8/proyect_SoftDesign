//Clase creada por IA 
package com.ecoembes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class PlantCommunicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PlantCommunicationException(String message) {
		super(message);
	}

	public PlantCommunicationException(String message, Throwable cause) {
		super(message, cause);
	}
}
