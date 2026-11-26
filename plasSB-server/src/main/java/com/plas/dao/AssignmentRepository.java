package com.plas.dao;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plas.domain.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

	// Contenedores ya comprometidos en una planta durante un día
	@Query("""
			select coalesce(sum(a.numContainers), 0)
			from Assignment a
			where a.plant.id = :plantId
			  and a.date = :date
			""")

}