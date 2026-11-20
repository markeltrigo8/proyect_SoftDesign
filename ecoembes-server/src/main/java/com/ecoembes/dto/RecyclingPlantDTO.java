package com.ecoembes.dto;

import java.time.LocalDate;
import java.util.Objects;

public class RecyclingPlantDTO {
	private int id;
	private String name;
	private LocalDate date;
	private double capacity;
	
	//CONSTRUCTOR
	
	public RecyclingPlantDTO() {
	}
	
	public RecyclingPlantDTO(int id, String name, LocalDate date, double capacity) {
		super();
		this.id = id;
		this.name = name;
		this.date = date;
		this.capacity = capacity;
	}
	
	//GETTERS Y SETTERS	
	
	
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
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public double getCapacity() {
		return capacity;
	}
	public void setCapacity(double capacity) {
		this.capacity = capacity;
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
		RecyclingPlantDTO other = (RecyclingPlantDTO) obj;
		return id == other.id;
	}
	
	
}
