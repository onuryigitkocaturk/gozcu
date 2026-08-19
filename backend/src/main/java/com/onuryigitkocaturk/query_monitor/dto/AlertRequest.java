package com.onuryigitkocaturk.query_monitor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class AlertRequest {

    @NotEmpty
    private List<UUID> groupIds;

    @NotNull
    @Valid
    private AlertConditionValue condition;

    public AlertRequest() {
    }

    public AlertRequest(List<UUID> groupIds, AlertConditionValue condition) {
        this.groupIds = groupIds;
        this.condition = condition;
    }

    public List<UUID> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<UUID> groupIds) {
        this.groupIds = groupIds;
    }

    public AlertConditionValue getCondition() {
        return condition;
    }

    public void setCondition(AlertConditionValue condition) {
        this.condition = condition;
    }
}
