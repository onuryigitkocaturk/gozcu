package com.onuryigitkocaturk.query_monitor.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlertResponse {

    private UUID id;
    private UUID queryId;
    private UUID projectId;
    private UUID groupId;
    private String groupName;
    private AlertConditionValue condition;
    private boolean active;
    private LocalDateTime createdAt;

    public AlertResponse() {
    }

    public AlertResponse(UUID id, UUID queryId, UUID projectId, UUID groupId, String groupName,
                          AlertConditionValue condition, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.queryId = queryId;
        this.projectId = projectId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.condition = condition;
        this.active = active;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQueryId() {
        return queryId;
    }

    public void setQueryId(UUID queryId) {
        this.queryId = queryId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public AlertConditionValue getCondition() {
        return condition;
    }

    public void setCondition(AlertConditionValue condition) {
        this.condition = condition;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
