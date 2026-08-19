package com.onuryigitkocaturk.query_monitor.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AlertResponse {

    private UUID id;
    private UUID queryId;
    private UUID projectId;
    private List<AlertGroupResponse> groups;
    private AlertConditionValue condition;
    private boolean active;
    private LocalDateTime createdAt;

    public AlertResponse() {
    }

    public AlertResponse(UUID id, UUID queryId, UUID projectId, List<AlertGroupResponse> groups,
                          AlertConditionValue condition, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.queryId = queryId;
        this.projectId = projectId;
        this.groups = groups;
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

    public List<AlertGroupResponse> getGroups() {
        return groups;
    }

    public void setGroups(List<AlertGroupResponse> groups) {
        this.groups = groups;
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
