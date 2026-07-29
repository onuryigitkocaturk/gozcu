package com.onuryigitkocaturk.query_monitor.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProjectTableResponse {

    private UUID id;
    private String tableName;
    private UUID projectId;
    private String projectName;
    private LocalDateTime createdAt;

    public ProjectTableResponse() {
    }

    public ProjectTableResponse(UUID id, String tableName, UUID projectId, String projectName,
                                 LocalDateTime createdAt) {
        this.id = id;
        this.tableName = tableName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
