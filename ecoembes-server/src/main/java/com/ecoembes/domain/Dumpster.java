package com.ecoembes.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Dumpster {

	@Id
	private int id;

	private String location;
	private int postalCode;
	private int capacity;
	private int numContainers;

	@Enumerated(EnumType.STRING)
	private FillLevel fillLevel;

	@OneToMany(mappedBy = "dumpster", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UsageRecord> usageHistory = new ArrayList<>();

	@ManyToMany(mappedBy = "dumpsters")
	private List<Assigment> assigments = new ArrayList<>();

	// Constructor requerido por JPA
	protected Dumpster() {
	}

	public Dumpster(int id, String location, int postalCode, int capacity, int numContainers, FillLevel fillLevel) {
		this.id = id;
		this.location = location;
		this.postalCode = postalCode;
		this.capacity = capacity;
		this.numContainers = numContainers;
		this.fillLevel = fillLevel;
	}

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

	// Mantiene sincronizados los dos lados de la relación
	public void addUsageRecord(UsageRecord record) {
		this.usageHistory.add(record);
		record.setDumpster(this);
	}

	public List<UsageRecord> getUsageHistory() {
		return new ArrayList<>(usageHistory);
	}

	public void addAssigment(Assigment a) {
		this.assigments.add(a);
	}

	public List<Assigment> getAssigments() {
		return assigments;
	}

	public void setAssigments(List<Assigment> assigments) {
		this.assigments = assigments;
	}
}
