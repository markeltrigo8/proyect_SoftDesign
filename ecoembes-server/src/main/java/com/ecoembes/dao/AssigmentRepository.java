package com.ecoembes.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecoembes.domain.Assigment;

public interface AssigmentRepository extends JpaRepository<Assigment, Long> {

	List<Assigment> findByEmployeeEmail(String email);

	// Capacidad ya comprometida en una planta durante un día
	// (start = inicio del día, end = inicio del día siguiente)
	@Query("""
			select coalesce(sum(a.totalContainers), 0)
			from Assigment a
			where a.recyclingPlant.id = :plantId
			  and a.date = :date
			""")
	int sumContainersByPlantAndDay(@Param("plantId") int plantId, @Param("date") LocalDate date);
}
