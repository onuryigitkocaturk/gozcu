package com.onuryigitkocaturk.query_monitor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class AlertRequest {

    @NotNull
    private Long groupId;

    @NotNull
    @Valid
    private AlertConditionValue condition;

    public AlertRequest() {
    }

    public AlertRequest(Long groupId, AlertConditionValue condition) {
        this.groupId = groupId;
        this.condition = condition;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public AlertConditionValue getCondition() {
        return condition;
    }

    public void setCondition(AlertConditionValue condition) {
        this.condition = condition;
    }
}
