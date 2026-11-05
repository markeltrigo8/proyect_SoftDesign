package com.ecoembes.domain;

import java.time.LocalDateTime;

public class UsageRecord {
	private LocalDateTime date;
	private int numContainers;
	private FillLevel fillLevel;
	
	//CONSTRUCTOR
	
	public UsageRecord(LocalDateTime date, int numContainers, FillLevel fillLevel) {
		super();
		this.date = date;
		this.numContainers = numContainers;
		this.fillLevel = fillLevel;
	}
	
	//GETTERS & SETTERS
	
	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
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
