package com.onuryigitkocaturk.query_monitor.dto;

import java.time.LocalDateTime;

public class AlertResponse {

    private Long id;
    private Long queryId;
    private Long projectId;
    private Long groupId;
    private String groupName;
    private AlertConditionValue condition;
    private boolean active;
    private LocalDateTime createdAt;

    public AlertResponse() {
    }

    public AlertResponse(Long id, Long queryId, Long projectId, Long groupId, String groupName,
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
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
