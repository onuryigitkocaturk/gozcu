package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import com.onuryigitkocaturk.query_monitor.enums.ConditionOperator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Tek bir koşul: bir kolon (field), bir işleç (operator) ve bir değer.
 * IS_NULL/IS_NOT_NULL için value gerekmez; bu durum servis katmaninda
 * ayrica dogrulanir (annotation ile ifade edilemeyecek kadar kosula bagli).
 */
public class ConditionNode implements QueryNode {

    @NotBlank
    private String field;

    @NotNull
    private ConditionOperator operator;

    @Valid
    private ConditionValue value;

    public ConditionNode() {
    }

    public ConditionNode(String field, ConditionOperator operator, ConditionValue value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public ConditionOperator getOperator() {
        return operator;
    }

    public void setOperator(ConditionOperator operator) {
        this.operator = operator;
    }

    public ConditionValue getValue() {
        return value;
    }

    public void setValue(ConditionValue value) {
        this.value = value;
    }
}
