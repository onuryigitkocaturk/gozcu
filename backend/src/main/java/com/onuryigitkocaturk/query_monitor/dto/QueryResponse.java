package com.onuryigitkocaturk.query_monitor.dto;

import com.onuryigitkocaturk.query_monitor.dto.querydefinition.QueryNode;
import com.onuryigitkocaturk.query_monitor.enums.Frequency;

import java.time.LocalDateTime;
import java.util.UUID;

public class QueryResponse {

    private UUID id;
    private String name;
    private Frequency frequency;
    private boolean active;
    private UUID projectId;
    private UUID projectTableId;
    private String tableName;
    private QueryNode definition;
    private LocalDateTime createdAt;
    private String createdByUsername;

    public QueryResponse() {
    }

    public QueryResponse(UUID id, String name, Frequency frequency, boolean active,
                          UUID projectId, UUID projectTableId, String tableName,
                          QueryNode definition, LocalDateTime createdAt, String createdByUsername) {
        this.id = id;
        this.name = name;
        this.frequency = frequency;
        this.active = active;
        this.projectId = projectId;
        this.projectTableId = projectTableId;
        this.tableName = tableName;
        this.definition = definition;
        this.createdAt = createdAt;
        this.createdByUsername = createdByUsername;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getProjectTableId() {
        return projectTableId;
    }

    public void setProjectTableId(UUID projectTableId) {
        this.projectTableId = projectTableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public QueryNode getDefinition() {
        return definition;
    }

    public void setDefinition(QueryNode definition) {
        this.definition = definition;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }
}
