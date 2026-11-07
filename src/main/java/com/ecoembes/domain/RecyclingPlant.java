package com.ecoembes.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecyclingPlant {
	private int id;
	private String name;
	private double capacity;
	private List<Assigment> assigments = new ArrayList<>();
	

	// CONSTRUCTOR

	public RecyclingPlant(int id, String name, double capacity) {
		super();
		this.id = id;
		this.name = name;
		this.capacity = capacity;
	}

	// GETTERS & SETTERS
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTotalCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}
	 public double getAssignedCapacity(LocalDateTime date) {
	     double result = 0;
	     for (Assigment a: assigments) {
	    	 if (a.getDate().equals(date)) {
	    		 result += a.getTotalContainers();
	    	 }
	     }
	     return result;
		 
	 }

}