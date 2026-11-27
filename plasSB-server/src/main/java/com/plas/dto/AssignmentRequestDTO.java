package com.plas.dto;

import java.time.LocalDate;
import java.util.List;

// Cuerpo de POST /assignments (lo envía PlasSBGateaway desde Ecoembes)
public class AssignmentRequestDTO {

	private LocalDate date;
	private int numDumpsters;
	private int numContainers;
	private List<Integer> dumpsterIds;

	public AssignmentRequestDTO() {
	}

	public AssignmentRequestDTO(LocalDate date, int numDumpsters, int numContainers, List<Integer> dumpsterIds) {
		this.date = date;
		this.numDumpsters = numDumpsters;
		this.numContainers = numContainers;
		this.dumpsterIds = dumpsterIds;
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

	public List<Integer> getDumpsterIds() {
		return dumpsterIds;
	}

	public void setDumpsterIds(List<Integer> dumpsterIds) {
		this.dumpsterIds = dumpsterIds;
	}
}