package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import jakarta.validation.constraints.NotNull;

public class LiteralValue implements ConditionValue {

    @NotNull
    private Object value;

    public LiteralValue() {
    }

    public LiteralValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
