package com.cont.dao;

import java.time.LocalDate;
import java.util.List;

import com.cont.domain.Assignment;

public interface AssignmentRepository {
	Assignment save(LocalDate date, int numDumpsters, int numContainers, List<String> dumpsterIds);
	int sumContainersByDate(LocalDate date);
}