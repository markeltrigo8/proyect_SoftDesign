package com.cont.domain;

import java.time.LocalDate;
import java.util.List;

public class Assignment {
	private final Long id;
	private final LocalDate date;
	private final int numDumpsters;
	private final int numContainers;
	private final List<String> dumpsterIds;

	public Assignment(Long id, LocalDate date, int numDumpsters, int numContainers, List<String> dumpsterIds) {
		this.id = id;
		this.date = date;
		this.numDumpsters = numDumpsters;
		this.numContainers = numContainers;
		this.dumpsterIds = List.copyOf(dumpsterIds);
	}

	public Long getId() { return id; }
	public LocalDate getDate() { return date; }
	public int getNumDumpsters() { return numDumpsters; }
	public int getNumContainers() { return numContainers; }
	public List<String> getDumpsterIds() { return dumpsterIds; }
}