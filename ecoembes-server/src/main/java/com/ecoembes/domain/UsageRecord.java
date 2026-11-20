package com.ecoembes.domain;

import java.time.LocalDateTime;

public class UsageRecord {
	private LocalDateTime date;
	private int numContainers;
	private FillLevel fillLevel;
	private Dumpster dumpster;
	
	//CONSTRUCTOR
	
	public UsageRecord(LocalDateTime date, int numContainers, FillLevel fillLevel, Dumpster dumpster) {
		super();
		this.date = date;
		this.numContainers = numContainers;
		this.fillLevel = fillLevel;
		this.dumpster = dumpster;
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

	public Dumpster getDumpster() {
		return dumpster;
	}

	public void setDumpster(Dumpster dumpster) {
		this.dumpster = dumpster;
	}
	
}
