//Clase creada por IA 
package com.ecoembes.gateaway;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class PlantCommunicationException extends RuntimeException {

	public PlantCommunicationException(String message) {
		super(message);
	}

	public PlantCommunicationException(String message, Throwable cause) {
		super(message, cause);
	}
}
