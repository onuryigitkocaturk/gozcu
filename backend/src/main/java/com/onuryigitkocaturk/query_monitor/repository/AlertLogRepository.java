package com.onuryigitkocaturk.query_monitor.repository;

import com.onuryigitkocaturk.query_monitor.enums.LogStatus;
import com.onuryigitkocaturk.query_monitor.model.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertLogRepository extends JpaRepository<AlertLog, UUID> {

    List<AlertLog> findByAlertId(UUID alertId);

    long countByAlert_ProjectIdAndStatusAndExecutedAtAfter(UUID projectId, LogStatus status, LocalDateTime after);

    Optional<AlertLog> findTopByAlert_ProjectIdAndStatusOrderByExecutedAtDesc(UUID projectId, LogStatus status);
}
