package com.onuryigitkocaturk.query_monitor.repository;

import com.onuryigitkocaturk.query_monitor.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM Alert a JOIN FETCH a.query q JOIN FETCH q.projectTable " +
            "JOIN FETCH a.project JOIN FETCH a.group WHERE a.query.id = :queryId")
    List<Alert> findByQueryId(@Param("queryId") Long queryId);

    List<Alert> findByProjectId(Long projectId);

    List<Alert> findByGroupId(Long groupId);

    List<Alert> findByActiveTrue();
}
