package com.cont.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.cont.domain.Assignment;

@Repository
public class InMemoryAssignmentRepository implements AssignmentRepository {

	private final List<Assignment> assignments = new CopyOnWriteArrayList<>();
	private final AtomicLong sequence = new AtomicLong();

	@Override
	public Assignment save(LocalDate date, int numDumpsters, int numContainers, List<String> dumpsterIds) {
		Assignment a = new Assignment(sequence.incrementAndGet(), date, numDumpsters, numContainers, dumpsterIds);
		assignments.add(a);
		return a;
	}

	@Override
	public int sumContainersByDate(LocalDate date) {
		return assignments.stream()
				.filter(a -> a.getDate().equals(date))
				.mapToInt(Assignment::getNumContainers)
				.sum();
	}
}