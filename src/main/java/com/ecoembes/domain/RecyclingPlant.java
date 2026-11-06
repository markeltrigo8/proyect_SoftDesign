package com.ecoembes.domain;

public class RecyclingPlant {
	private String id;
	private String name;
	private double capacity;

	// CONSTRUCTOR

	public RecyclingPlant(String id, String name, double capacity) {
		super();
		this.id = id;
		this.name = name;
		this.capacity = capacity;
	}

	// GETTERS & SETTERS
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

}