package com.ecoembes.domain;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.aspectj.weaver.Dump;

public class Assigment {
	private LocalDateTime date;
	private int totalDumpsters;
	private int totalContainers;
	private Employee employee;
	private RecyclingPlant recyclingPlant;
	private List<Dumpster> dumpsters;
	// CONSTRUCTOR
	public Assigment(LocalDateTime date, int totalDumpsters, int totalContainers, Employee employee, RecyclingPlant rp, List<Dumpster> dumpsters) {
		super();
		this.date = date;
		this.totalDumpsters = totalDumpsters;
		this.totalContainers = totalContainers;
		this.employee = employee;
		this.recyclingPlant = rp;
		this.dumpsters = dumpsters;
	}
	
	// GETTERS & SETTERS
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
	public Employee getEmployee () {
		return this.employee;
	}
	public void setEmployee (Employee e) {
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
