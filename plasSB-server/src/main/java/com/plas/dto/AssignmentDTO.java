package com.plas.dto;

import java.time.LocalDate;
import java.util.List;

// Respuesta (201) de POST /assignments: la asignación ya registrada
public class AssignmentDTO {

	private Long id;
	private LocalDate date;
	private int numDumpsters;
	private int numContainers;
	private List<String> dumpsterIds;

	public AssignmentDTO() {
	}

	public AssignmentDTO(Long id, LocalDate date, int numDumpsters, int numContainers, List<String> dumpsterIds) {
		this.id = id;
		this.date = date;
		this.numDumpsters = numDumpsters;
		this.numContainers = numContainers;
		this.dumpsterIds = dumpsterIds;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
}
