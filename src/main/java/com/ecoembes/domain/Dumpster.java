package com.ecoembes.domain;

import java.util.ArrayList;
import java.util.List;

public class Dumpster {
	private int id;
	private String location;
	private int postalCode;
	private int capacity;
	private int numContainers;
	private FillLevel fillLevel;
	private List<UsageRecord> usageHistory = new ArrayList<>();
	private List<Assigment> assigments = new ArrayList<>();
	
	
	//CONSTRUCTOR
	
	public Dumpster(int id, String location, int postalCode, int capacity, int numContainers, FillLevel fillLevel) {
		super();
		this.id = id;
		this.location = location;
		this.postalCode = postalCode;
		this.capacity = capacity;
		this.numContainers = numContainers;
		this.fillLevel = fillLevel;
	}

	// GETTERS & SETTERS
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public int getPostalCode() {
		return postalCode;
	}


	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}


	public int getCapacity() {
		return capacity;
	}


	public void setCapacity(int capacity) {
		this.capacity = capacity;
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

	// HISTORIAL DE USO

	public void addUsageRecord(UsageRecord record) {
		this.usageHistory.add(record);
	}

	public List<UsageRecord> getUsageHistory() {
		return new ArrayList<>(usageHistory);
	}

	public List<Assigment> getAssigments() {
		return assigments;
	}

	public void setAssigments(List<Assigment> assigments) {
		this.assigments = assigments;
	}
}