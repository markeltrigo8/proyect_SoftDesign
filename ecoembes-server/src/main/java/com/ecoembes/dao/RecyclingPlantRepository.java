package com.ecoembes.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecoembes.domain.RecyclingPlant;

import jakarta.persistence.LockModeType;

public interface RecyclingPlantRepository extends JpaRepository<RecyclingPlant, Integer> {

	// Bloquea la fila de la planta hasta que acabe la transacción, para que dos
	// asignaciones simultáneas no pasen la comprobación de capacidad a la vez.
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from RecyclingPlant p where p.id = :id")
	Optional<RecyclingPlant> findByIdForUpdate(@Param("id") int id);
}