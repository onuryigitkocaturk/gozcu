package com.onuryigitkocaturk.query_monitor.alerting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onuryigitkocaturk.query_monitor.connector.QueryExecutionService;
import com.onuryigitkocaturk.query_monitor.dto.AlertConditionValue;
import com.onuryigitkocaturk.query_monitor.enums.ConditionOperator;
import org.springframework.stereotype.Service;

/**
 * Bir Alert'in su an tetiklenip tetiklenmeyecegini hesaplar: Query'yi
 * calistirip eslesen satir sayisini alir, Alert'in kosuluyla (metric +
 * operator + esik deger) karsilastirir. Mail GONDERMEZ - sadece
 * "tetiklenir miydi" sorusuna cevap verir; mail gonderimi scheduler +
 * notification/ paketinin isi (sonraki artis).
 */
@Service
public class AlertEvaluationService {

    private final QueryExecutionService queryExecutionService;
    private final ObjectMapper objectMapper;

    public AlertEvaluationService(QueryExecutionService queryExecutionService, ObjectMapper objectMapper) {
        this.queryExecutionService = queryExecutionService;
        this.objectMapper = objectMapper;
    }

    public AlertEvaluationResult evaluate(String tableName, String queryDefinitionJson, String conditionExpressionJson) {
        long matchCount = queryExecutionService.countMatches(tableName, queryDefinitionJson);
        AlertConditionValue condition = parseCondition(conditionExpressionJson);
        boolean triggered = compare(matchCount, condition.getOperator(), condition.getValue());
        return new AlertEvaluationResult(triggered, matchCount);
    }

    private AlertConditionValue parseCondition(String conditionExpressionJson) {
        try {
            return objectMapper.readValue(conditionExpressionJson, AlertConditionValue.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Stored alert condition is corrupted", e);
        }
    }

    private boolean compare(long matchCount, ConditionOperator operator, long threshold) {
        return switch (operator) {
            case EQUALS -> matchCount == threshold;
            case NOT_EQUALS -> matchCount != threshold;
            case GREATER_THAN -> matchCount > threshold;
            case GREATER_THAN_OR_EQUAL -> matchCount >= threshold;
            case LESS_THAN -> matchCount < threshold;
            case LESS_THAN_OR_EQUAL -> matchCount <= threshold;
            case CONTAINS, IS_NULL, IS_NOT_NULL -> throw new IllegalStateException(
                    "Operator not supported for alert condition: " + operator);
        };
    }
}
