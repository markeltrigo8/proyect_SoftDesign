package com.ecoembes.dto;

import java.time.LocalDate;
import java.util.Objects;

public class DumpsterUsageDTO {
	LocalDate date;
	int numContainers;
	String fillLevel;
	
	//CONSTRUCTOR
	
	public DumpsterUsageDTO(LocalDate date, int numContainers, String fillLevel) {
		this.date = date;
		this.numContainers = numContainers;
		this.fillLevel = fillLevel;
	}
	
	//GETTERS AND SETTERS
	
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
	public String getFillLevel() {
		return fillLevel;
	}
	public void setFillLevel(String fillLevel) {
		this.fillLevel = fillLevel;
	}
	@Override
	public int hashCode() {
		return Objects.hash(date);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DumpsterUsageDTO other = (DumpsterUsageDTO) obj;
		return Objects.equals(date, other.date);
	}
	
	
}
