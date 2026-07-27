package com.onuryigitkocaturk.query_monitor.querybuilder;

import com.onuryigitkocaturk.query_monitor.dto.querydefinition.ConditionNode;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.GroupNode;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.QueryNode;
import com.onuryigitkocaturk.query_monitor.enums.ConditionOperator;
import com.onuryigitkocaturk.query_monitor.exception.InvalidQueryDefinitionException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Surukle-birak agacini (QueryNode) recursive olarak dolasip iki seyi
 * dogrular: (1) her ConditionNode.field, hedef tablonun GERCEK bir kolonu mu
 * (whitelist - SQL'e eklenmeden once burada durdurulmali), (2) operator ile
 * value tutarli mi (orn. IS_NULL bir value ISTEMEZ, GREATER_THAN ISTER).
 */
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
            throw new InvalidQueryDefinitionException("Unknown column: " + condition.getField());
        }

        boolean requiresValue = condition.getOperator() != ConditionOperator.IS_NULL
                && condition.getOperator() != ConditionOperator.IS_NOT_NULL;

        if (requiresValue && condition.getValue() == null) {
            throw new InvalidQueryDefinitionException(
                    "Value required for operator " + condition.getOperator()
                            + " on field " + condition.getField());
        }
    }
}
