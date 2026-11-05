package com.ecoembes.domain;

import java.time.LocalDate;

public class UsageRecord {
	private LocalDate date;
	private int numContainers;
	private FillLevel fillLevel;
	
	//CONSTRUCTOR
	
	public UsageRecord(LocalDate date, int numContainers, FillLevel fillLevel) {
		super();
		this.date = date;
		this.numContainers = numContainers;
		this.fillLevel = fillLevel;
	}
	
	//GETTERS & SETTERS
	
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getNumContainers() {
		return numContainers;
	}

	public void setNumContainers(int numContainers) {
		this.numContainers = numContainers;
	}

	public FillLevel getFillLevel() {
		return fillLevel;
	}

	public void setFillLevel(FillLevel fillLevel) {
		this.fillLevel = fillLevel;
	}	
}
