package com.onuryigitkocaturk.query_monitor.repository;

import com.onuryigitkocaturk.query_monitor.model.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlertLogRepository extends JpaRepository<AlertLog, UUID> {

    List<AlertLog> findByAlertId(UUID alertId);
}
