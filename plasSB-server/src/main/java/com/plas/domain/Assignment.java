package com.plas.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// Asignación de contenedores que Ecoembes notifica a la planta
@Entity
public class Assignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "assignment_date")
	private LocalDate date;

	private int numDumpsters;
	private int numContainers;

	// Ids de los dumpsters asignados (tabla auxiliar assignment_dumpster)
	@ElementCollection
	@CollectionTable(name = "assignment_dumpster", joinColumns = @JoinColumn(name = "assignment_id"))
	@Column(name = "dumpster_id")
	private List<String> dumpsterIds = new ArrayList<>();

	// Este lado guarda la clave foránea
	@ManyToOne(optional = false)
	@JoinColumn(name = "plant_id")
	private RecyclingPlant plant;

	// Constructor requerido por JPA
	protected Assignment() {
	}

	public Assignment(LocalDate date, int numDumpsters, int numContainers, List<String> dumpsterIds, RecyclingPlant plant) {
		this.date = date;
		this.numDumpsters = numDumpsters;
		this.numContainers = numContainers;
		this.dumpsterIds = dumpsterIds == null ? new ArrayList<>() : new ArrayList<>(dumpsterIds);
		this.plant = plant;
	}

	public Long getId() {
		return id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getNumDumpsters() {
		return numDumpsters;
	}

	public void setNumDumpsters(int numDumpsters) {
		this.numDumpsters = numDumpsters;
	}

	public int getNumContainers() {
		return numContainers;
	}

	public void setNumContainers(int numContainers) {
		this.numContainers = numContainers;
	}

	public List<String> getDumpsterIds() {
		return dumpsterIds;
	}

	public void setDumpsterIds(List<String> dumpsterIds) {
		this.dumpsterIds = dumpsterIds;
	}

	public RecyclingPlant getPlant() {
		return plant;
	}

	public void setPlant(RecyclingPlant plant) {
		this.plant = plant;
	}
}