package com.onuryigitkocaturk.query_monitor.repository;

import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTableRepository extends JpaRepository<ProjectTable, Long> {

    List<ProjectTable> findByProjectId(Long projectId);

    boolean existsByProjectIdAndTableName(Long projectId, String tableName);
}
