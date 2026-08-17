package com.onuryigitkocaturk.query_monitor.alerting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onuryigitkocaturk.query_monitor.connector.ConnectionDetails;
import com.onuryigitkocaturk.query_monitor.connector.QueryExecutionService;
import com.onuryigitkocaturk.query_monitor.dto.AlertConditionValue;
import com.onuryigitkocaturk.query_monitor.enums.ConditionOperator;
import org.springframework.stereotype.Service;

// bir alert'in anlık tetiklenip tetiklenmeyeceğini hesaplar.
// query'yi çalıştırıp eşleşen satır sayısını alır, alert koşulu
// ile karşılaştırır.
@Service
public class AlertEvaluationService {

    private final QueryExecutionService queryExecutionService; // izlenen veritabanına karşı gerçek sql sorguları çalıştıran servis
    private final ObjectMapper objectMapper; // jackson'a ait json -> java sınıfı çeviricisi

    public AlertEvaluationService(QueryExecutionService queryExecutionService, ObjectMapper objectMapper) {
        this.queryExecutionService = queryExecutionService;
        this.objectMapper = objectMapper;
    }
    // dışarıya açılan fonksiyon, alert'i değerlendirip sonucu döndürür.
    // parametreler: hangi tabloya bakılacak, query'nin filtresi, alert'in koşulu
    // manuel değerlendir butonu ve AlertSchedular bu fonksiyonu kullanıyor.
    public AlertEvaluationResult evaluate(ConnectionDetails connection, String tableName,
                                           String queryDefinitionJson, String conditionExpressionJson) {
        long matchCount = queryExecutionService.countMatches(connection, tableName, queryDefinitionJson);
        AlertConditionValue condition = parseCondition(conditionExpressionJson);
        boolean triggered = compare(matchCount, condition.getOperator(), condition.getValue());
        return new AlertEvaluationResult(triggered, matchCount);
    }

    // JSON stringi kullanılabilir bir AlertConditionValue nesnesine çevirir -> deserialization?
    private AlertConditionValue parseCondition(String conditionExpressionJson) {
        try {
            return objectMapper.readValue(conditionExpressionJson, AlertConditionValue.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Stored alert condition is corrupted", e);
        }
    }

    // matchCount'u operatöre göre threshold ile kıyaslar
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
