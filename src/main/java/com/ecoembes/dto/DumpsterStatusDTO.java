package com.ecoembes.dto;

import java.time.LocalDateTime;

public class DumpsterStatusDTO {
	private int postalCode;
	private LocalDateTime date;
	
	//CONSTRUCTOR
	
	public DumpsterStatusDTO(int postalCode, LocalDateTime date) {
		super();
		this.postalCode = postalCode;
		this.date = date;
	}

	//GETTERS AND SETTERS
	
	public int getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	
}
