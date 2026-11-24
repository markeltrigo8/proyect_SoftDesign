package com.ecoembes.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class RecyclingPlant {

	@Id
	private int id;

	private String name;
	private double capacity;

	@OneToMany(mappedBy = "recyclingPlant")
	private List<Assigment> assigments = new ArrayList<>();

	// Constructor requerido por JPA
	protected RecyclingPlant() {
	}

	public RecyclingPlant(int id, String name, double capacity) {
		this.id = id;
		this.name = name;
		this.capacity = capacity;
	}

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

	public void addAssigment(Assigment a) {
		this.assigments.add(a);
	}

	public List<Assigment> getAssigments() {
		return assigments;
	}

	// La capacidad asignada por día se calcula ahora con
	// AssigmentRepository.sumContainersByPlantAndDay(...)
}
