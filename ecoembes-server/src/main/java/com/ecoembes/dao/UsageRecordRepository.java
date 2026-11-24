package com.ecoembes.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoembes.domain.UsageRecord;

public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {

	List<UsageRecord> findByDumpsterIdAndDateBetweenOrderByDateAscIdAsc(int dumpsterId, LocalDateTime from, LocalDateTime to);

	// Último registro de un dumpster dentro de un intervalo (p. ej. un día)
	Optional<UsageRecord> findFirstByDumpsterIdAndDateBetweenOrderByDateDescIdDesc(int dumpsterId, LocalDateTime from, LocalDateTime to);
}
