package com.ecoembes.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Assigment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime date;
	private int totalDumpsters;
	private int totalContainers;

	// Este lado guarda las claves foráneas
	@ManyToOne(optional = false)
	@JoinColumn(name = "employee_email")
	private Employee employee;

	@ManyToOne(optional = false)
	@JoinColumn(name = "recycling_plant_id")
	private RecyclingPlant recyclingPlant;

	@ManyToMany
	@JoinTable(name = "assigment_dumpster",
			joinColumns = @JoinColumn(name = "assigment_id"),
			inverseJoinColumns = @JoinColumn(name = "dumpster_id"))
	private List<Dumpster> dumpsters = new ArrayList<>();

	// Constructor requerido por JPA
	protected Assigment() {
	}

	public Assigment(LocalDateTime date, int totalDumpsters, int totalContainers, Employee employee,
			RecyclingPlant rp, List<Dumpster> dumpsters) {
		this.date = date;
		this.totalDumpsters = totalDumpsters;
		this.totalContainers = totalContainers;
		this.employee = employee;
		this.recyclingPlant = rp;
		this.dumpsters = dumpsters;
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

	public int getTotalDumpsters() {
		return totalDumpsters;
	}

	public void setTotalDumpsters(int totalDumpsters) {
		this.totalDumpsters = totalDumpsters;
	}

	public int getTotalContainers() {
		return totalContainers;
	}

	public void setTotalContainers(int totalContainers) {
		this.totalContainers = totalContainers;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee e) {
		this.employee = e;
	}

	public RecyclingPlant getRecyclingPlant() {
		return recyclingPlant;
	}

	public void setRecyclingPlant(RecyclingPlant recyclingPlant) {
		this.recyclingPlant = recyclingPlant;
	}

	public List<Dumpster> getDumpsters() {
		return dumpsters;
	}

	public void setDumpsters(List<Dumpster> dumpsters) {
		this.dumpsters = dumpsters;
	}
}
