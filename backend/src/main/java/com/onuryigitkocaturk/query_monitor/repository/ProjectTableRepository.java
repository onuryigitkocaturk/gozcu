package com.onuryigitkocaturk.query_monitor.repository;

import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectTableRepository extends JpaRepository<ProjectTable, UUID> {

    @Query("SELECT pt FROM ProjectTable pt JOIN FETCH pt.project WHERE pt.project.id = :projectId")
    List<ProjectTable> findByProjectId(@Param("projectId") UUID projectId);

    boolean existsByProjectIdAndTableName(UUID projectId, String tableName);

    long countByProjectId(UUID projectId);
}
