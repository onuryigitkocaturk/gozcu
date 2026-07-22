package com.onuryigitkocaturk.query_monitor.repository;

import com.onuryigitkocaturk.query_monitor.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByQueryId(Long queryId);

    List<Alert> findByGroupId(Long groupId);

    List<Alert> findByActiveTrue();
}
