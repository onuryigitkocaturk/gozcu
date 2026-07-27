package com.onuryigitkocaturk.query_monitor.querybuilder;

import com.onuryigitkocaturk.query_monitor.dto.querydefinition.ConditionNode;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.ConditionValue;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.GroupNode;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.LiteralValue;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.QueryNode;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.RelativeDateValue;
import com.onuryigitkocaturk.query_monitor.enums.ConditionOperator;
import com.onuryigitkocaturk.query_monitor.enums.LogicOperator;
import com.onuryigitkocaturk.query_monitor.enums.RelativeDateDirection;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * QueryNode agacini (surukle-birak JSON'unun Java karsiligi) parametreli bir
 * SQL WHERE kosuluna cevirir. Kolon isimleri (field) cagiran taraf tarafindan
 * ONCEDEN whitelist kontrolunden gecirilmis olmalidir (bkz.
 * QueryDefinitionValidator) - burada tekrar dogrulanmaz, cunku bu sinif
 * sadece SAKLANMIS (daha once olusturma anida dogrulanmis) tanimlar
 * uzerinde calisir. Degerler HICBIR ZAMAN SQL'e concat edilmez, hep '?' ile
 * baglanir.
 */
@Component
public class QuerySqlBuilder {

    public SqlFragment build(QueryNode node) {
        if (node instanceof GroupNode group) {
            return buildGroup(group);
        }
        if (node instanceof ConditionNode condition) {
            return buildCondition(condition);
        }
        throw new IllegalStateException("Unknown query node type: " + node.getClass());
    }

    private SqlFragment buildGroup(GroupNode group) {
        List<Object> parameters = new ArrayList<>();
        String joiner = group.getLogic() == LogicOperator.AND ? " AND " : " OR ";

        String sql = group.getChildren().stream()
                .map(child -> {
                    SqlFragment childFragment = build(child);
                    parameters.addAll(childFragment.parameters());
                    return "(" + childFragment.sql() + ")";
                })
                .collect(Collectors.joining(joiner));

        return new SqlFragment(sql, parameters);
    }

    private SqlFragment buildCondition(ConditionNode condition) {
        String field = condition.getField();
        ConditionOperator operator = condition.getOperator();

        if (operator == ConditionOperator.IS_NULL) {
            return new SqlFragment(field + " IS NULL", List.of());
        }
        if (operator == ConditionOperator.IS_NOT_NULL) {
            return new SqlFragment(field + " IS NOT NULL", List.of());
        }

        Object resolvedValue = resolveValue(condition.getValue());

        if (operator == ConditionOperator.CONTAINS) {
            return new SqlFragment(field + " LIKE ?", List.of("%" + resolvedValue + "%"));
        }

        return new SqlFragment(field + " " + mapOperator(operator) + " ?", List.of(resolvedValue));
    }

    private String mapOperator(ConditionOperator operator) {
        return switch (operator) {
            case EQUALS -> "=";
            case NOT_EQUALS -> "<>";
            case GREATER_THAN -> ">";
            case GREATER_THAN_OR_EQUAL -> ">=";
            case LESS_THAN -> "<";
            case LESS_THAN_OR_EQUAL -> "<=";
            case CONTAINS -> "LIKE";
            case IS_NULL, IS_NOT_NULL -> throw new IllegalStateException(
                    "IS_NULL/IS_NOT_NULL value gerektirmez, buraya ulasmamali: " + operator);
        };
    }

    private Object resolveValue(ConditionValue value) {
        if (value instanceof LiteralValue literal) {
            return literal.getValue();
        }
        if (value instanceof RelativeDateValue relativeDate) {
            return resolveRelativeDate(relativeDate);
        }
        throw new IllegalStateException("Unknown condition value type: " + value.getClass());
    }

    /**
     * RELATIVE_DATE, her calistirmada "su an" baz alinarak YENIDEN
     * hesaplanir - sorgu olusturuldugu andaki tarihe DONDURULMEZ.
     */
    private LocalDateTime resolveRelativeDate(RelativeDateValue value) {
        LocalDateTime now = LocalDateTime.now();
        ChronoUnit unit = switch (value.getUnit()) {
            case HOURS -> ChronoUnit.HOURS;
            case DAYS -> ChronoUnit.DAYS;
            case MONTHS -> ChronoUnit.MONTHS;
            case YEARS -> ChronoUnit.YEARS;
        };

        return value.getDirection() == RelativeDateDirection.FUTURE
                ? now.plus(value.getAmount(), unit)
                : now.minus(value.getAmount(), unit);
    }
}
