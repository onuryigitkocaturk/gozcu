package com.onuryigitkocaturk.query_monitor.dto;

import com.onuryigitkocaturk.query_monitor.enums.AlertMetric;
import com.onuryigitkocaturk.query_monitor.enums.ConditionOperator;
import jakarta.validation.constraints.NotNull;

/**
 * Alert.conditionExpression alaninda JSON olarak saklanan yapi:
 * "eslesen satir sayisi (metric) X isleciyle (operator) Y esigine (value)
 * gore degerlendirilsin".
 */
public class AlertConditionValue {

    @NotNull
    private AlertMetric metric;

    @NotNull
    private ConditionOperator operator;

    @NotNull
    private Long value;

    public AlertConditionValue() {
    }

    public AlertConditionValue(AlertMetric metric, ConditionOperator operator, Long value) {
        this.metric = metric;
        this.operator = operator;
        this.value = value;
    }

    public AlertMetric getMetric() {
        return metric;
    }

    public void setMetric(AlertMetric metric) {
        this.metric = metric;
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
