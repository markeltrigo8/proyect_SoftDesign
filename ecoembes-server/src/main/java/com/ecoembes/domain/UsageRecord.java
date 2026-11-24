package com.ecoembes.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class UsageRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime date;
	private int numContainers;

	@Enumerated(EnumType.STRING)
	private FillLevel fillLevel;

	@ManyToOne(optional = false)
	@JoinColumn(name = "dumpster_id")
	private Dumpster dumpster;

	// Constructor requerido por JPA
	protected UsageRecord() {
	}

	public UsageRecord(LocalDateTime date, int numContainers, FillLevel fillLevel, Dumpster dumpster) {
		this.date = date;
		this.numContainers = numContainers;
		this.fillLevel = fillLevel;
		this.dumpster = dumpster;
	}

	public Long getId() {
		return id;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
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

	public Dumpster getDumpster() {
		return dumpster;
	}

	public void setDumpster(Dumpster dumpster) {
		this.dumpster = dumpster;
	}
}
