package com.onuryigitkocaturk.query_monitor.dto;

import jakarta.validation.constraints.NotBlank;

public class ProjectTableRequest {

    @NotBlank
    private String tableName;

    public ProjectTableRequest() {
    }

    public ProjectTableRequest(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
