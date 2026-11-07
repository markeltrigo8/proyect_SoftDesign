package com.ecoembes.dto;

import java.util.Objects;

import com.ecoembes.domain.FillLevel;

public class DumpsterDTO {
	private int id;
	private String location;
	private int postalCode;
	private int capacity;
	private int numContainers;
	private FillLevel fillLevel;
	
	//CONSTRUCTOR
	
	public DumpsterDTO(int id, String location, int postalCode, int capacity, int numContainers, FillLevel fillLevel) {
		this.id = id;
		this.location = location;
		this.postalCode = postalCode;
		this.capacity = capacity;
		this.numContainers = numContainers;
		this.fillLevel = fillLevel;
		}
	
	//GETTERS AND SETTERS
	
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
	@Override
	public int hashCode() {
		return Objects.hash(Integer.valueOf(id));
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DumpsterDTO other = (DumpsterDTO) obj;
		return id == other.id;
	}
	
	
	
}
