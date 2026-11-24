package com.ecoembes.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecoembes.domain.Dumpster;

import jakarta.persistence.LockModeType;

public interface DumpsterRepository extends JpaRepository<Dumpster, Integer> {

	List<Dumpster> findByPostalCode(int postalCode);

	// Bloquea la fila del dumpster mientras dura la transacción (sustituye al synchronized)
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select d from Dumpster d where d.id = :id")
	Optional<Dumpster> findByIdForUpdate(@Param("id") int id);
}