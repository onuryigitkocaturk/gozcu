package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import com.onuryigitkocaturk.query_monitor.enums.RelativeDateDirection;
import com.onuryigitkocaturk.query_monitor.enums.RelativeDateUnit;
import jakarta.validation.constraints.NotNull;

public class RelativeDateValue implements ConditionValue {

    @NotNull
    private RelativeDateDirection direction;

    @NotNull
    private Integer amount;

    @NotNull
    private RelativeDateUnit unit;

    public RelativeDateValue() {
    }

    public RelativeDateValue(RelativeDateDirection direction, Integer amount, RelativeDateUnit unit) {
        this.direction = direction;
        this.amount = amount;
        this.unit = unit;
    }

    public RelativeDateDirection getDirection() {
        return direction;
    }

    public void setDirection(RelativeDateDirection direction) {
        this.direction = direction;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public RelativeDateUnit getUnit() {
        return unit;
    }

    public void setUnit(RelativeDateUnit unit) {
        this.unit = unit;
    }
}
