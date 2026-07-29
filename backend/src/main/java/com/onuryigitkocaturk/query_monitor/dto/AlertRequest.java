package com.onuryigitkocaturk.query_monitor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AlertRequest {

    @NotNull
    private UUID groupId;

    @NotNull
    @Valid
    private AlertConditionValue condition;

    public AlertRequest() {
    }

    public AlertRequest(UUID groupId, AlertConditionValue condition) {
        this.groupId = groupId;
        this.condition = condition;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public AlertConditionValue getCondition() {
        return condition;
    }

    public void setCondition(AlertConditionValue condition) {
        this.condition = condition;
    }
}
