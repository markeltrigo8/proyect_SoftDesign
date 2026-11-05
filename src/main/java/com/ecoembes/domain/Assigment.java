package com.ecoembes.domain;

import java.time.LocalDate;

public class Assigment {
	private LocalDate date;
	private int totalDumpsters;
	private int totalContainers;
	// CONSTRUCTOR
	public Assigment(LocalDate date, int totalDumpsters, int totalContainers) {
		super();
		this.date = date;
		this.totalDumpsters = totalDumpsters;
		this.totalContainers = totalContainers;
	}
	// GETTERS & SETTERS
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
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
	
}
