package com.onuryigitkocaturk.query_monitor.querybuilder;

import com.onuryigitkocaturk.query_monitor.dto.querydefinition.ConditionNode;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.GroupNode;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.QueryNode;
import com.onuryigitkocaturk.query_monitor.enums.ConditionOperator;
import com.onuryigitkocaturk.query_monitor.exception.InvalidQueryDefinitionException;
import org.springframework.stereotype.Component;

import java.util.List;

// bir query kaydedilmeden önce çalışan güvenlik kontrolüdür.
// sürükle-bırak ağacını recursive gezip iki şeyi doğruluyor:
// 1: her field'ın gerçek bir kolon olduğu
// 2: operator/value uyumu.

@Component
public class QueryDefinitionValidator {

    public void validate(QueryNode node, List<String> validColumns) {
        if (node instanceof GroupNode group) {
            for (QueryNode child : group.getChildren()) {
                validate(child, validColumns);
            }
        } else if (node instanceof ConditionNode condition) {
            validateCondition(condition, validColumns);
        }
    }

    private void validateCondition(ConditionNode condition, List<String> validColumns) {
        if (!validColumns.contains(condition.getField())) {
            throw new InvalidQueryDefinitionException("Bilinmeyen kolon: " + condition.getField());
        }

        boolean requiresValue;
        if (condition.getOperator() == ConditionOperator.IS_NULL) {
            requiresValue = false;
        } else if (condition.getOperator() == ConditionOperator.IS_NOT_NULL) {
            requiresValue = false;
        } else {
            requiresValue = true;
        }

        if (requiresValue && condition.getValue() == null) {
            throw new InvalidQueryDefinitionException(
                    condition.getField() + " alanındaki " + condition.getOperator()
                            + " işleci için bir değer girilmeli");
        }
    }
}
