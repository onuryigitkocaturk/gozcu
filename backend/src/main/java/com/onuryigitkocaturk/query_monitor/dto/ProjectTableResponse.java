package com.onuryigitkocaturk.query_monitor.dto;

import java.time.LocalDateTime;

public class ProjectTableResponse {

    private Long id;
    private String tableName;
    private LocalDateTime createdAt;

    public ProjectTableResponse() {
    }

    public ProjectTableResponse(Long id, String tableName, LocalDateTime createdAt) {
        this.id = id;
        this.tableName = tableName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
