//Creado con IA
package com.ecoembes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InsufficientCapacityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InsufficientCapacityException(String message) {
		super(message);
	}
}