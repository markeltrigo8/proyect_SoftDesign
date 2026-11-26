package com.plas.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plas.domain.RecyclingPlant;

import jakarta.persistence.LockModeType;

public interface RecyclingPlantRepository extends JpaRepository<RecyclingPlant, Integer> {

	// Bloquea la fila de la planta hasta el fin de la transacción: dos notificaciones
	// simultáneas no pueden pasar la comprobación de capacidad a la vez.
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Plant p where p.id = :id")
	Optional<RecyclingPlant> findByIdForUpdate(@Param("id") int id);
}