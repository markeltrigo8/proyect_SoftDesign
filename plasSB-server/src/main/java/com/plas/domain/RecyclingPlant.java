package com.plas.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

// La planta PlasSB: guarda su capacidad total (la capacidad libre de cada día se calcula)
@Entity
public class RecyclingPlant {

	@Id
	private int id;

	private String name;

	// Contenedores que la planta puede procesar al día
	private int capacity;

	@OneToMany(mappedBy = "plant")
	private List<Assignment> assignments = new ArrayList<>();

	// Constructor requerido por JPA
	protected RecyclingPlant() {
	}

	public RecyclingPlant(int id, String name, int capacity) {
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

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public List<Assignment> getAssignments() {
		return assignments;
	}
}