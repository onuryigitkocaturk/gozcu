package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import com.onuryigitkocaturk.query_monitor.enums.ConditionOperator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// sürükle-bırak ekranda kurduğum her tek kutu burası,
// örneğin, "km büyüktür 1000" (hangi kolon + hangi işleç + hangi değer)
// imlplements QueryNode GroupNode'da olduğu gibi children listesine konabilecek
// bir tip olduğunu garanti ediyor.
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
