package com.ecoembes.dto;

import java.time.LocalDate;

public class DumpsterStatusDTO {
	private int postalCode;
	private LocalDate date;
	
	//CONSTRUCTOR
	
	public DumpsterStatusDTO() {
	}
	
	public DumpsterStatusDTO(int postalCode, LocalDate date) {
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	
}
