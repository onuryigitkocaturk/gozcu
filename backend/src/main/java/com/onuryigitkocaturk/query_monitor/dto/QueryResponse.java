package com.onuryigitkocaturk.query_monitor.dto;

import com.onuryigitkocaturk.query_monitor.dto.querydefinition.QueryNode;
import com.onuryigitkocaturk.query_monitor.enums.Frequency;

import java.time.LocalDateTime;

public class QueryResponse {

    private Long id;
    private String name;
    private Frequency frequency;
    private boolean active;
    private Long projectId;
    private Long projectTableId;
    private String tableName;
    private QueryNode definition;
    private LocalDateTime createdAt;

    public QueryResponse() {
    }

    public QueryResponse(Long id, String name, Frequency frequency, boolean active,
                          Long projectId, Long projectTableId, String tableName,
                          QueryNode definition, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.frequency = frequency;
        this.active = active;
        this.projectId = projectId;
        this.projectTableId = projectTableId;
        this.tableName = tableName;
        this.definition = definition;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectTableId() {
        return projectTableId;
    }

    public void setProjectTableId(Long projectTableId) {
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
}
