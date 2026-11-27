package com.cont.exception;

public class RequestException extends RuntimeException {
	private final int code;

	public RequestException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() { return code; }
}