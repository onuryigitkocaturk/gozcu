package com.onuryigitkocaturk.query_monitor.dto;

import com.onuryigitkocaturk.query_monitor.enums.ConditionOperator;
import jakarta.validation.constraints.NotNull;

// Alert.conditioExpression alanında JSON olarak saklanan yapı.
public class AlertConditionValue {

    @NotNull
    private ConditionOperator operator;

    @NotNull
    private Long value;

    public AlertConditionValue() {
    }

    public AlertConditionValue(ConditionOperator operator, Long value) {
        this.operator = operator;
        this.value = value;
    }

    public ConditionOperator getOperator() {
        return operator;
    }

    public void setOperator(ConditionOperator operator) {
        this.operator = operator;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
