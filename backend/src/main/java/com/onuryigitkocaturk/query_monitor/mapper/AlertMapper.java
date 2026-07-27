package com.onuryigitkocaturk.query_monitor.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onuryigitkocaturk.query_monitor.dto.AlertConditionValue;
import com.onuryigitkocaturk.query_monitor.dto.AlertResponse;
import com.onuryigitkocaturk.query_monitor.model.Alert;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {

    private final ObjectMapper objectMapper;

    public AlertMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AlertResponse toResponse(Alert alert) {
        AlertConditionValue condition;
        try {
            condition = objectMapper.readValue(alert.getConditionExpression(), AlertConditionValue.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Stored alert condition is corrupted for alert id " + alert.getId(), e);
        }

        return new AlertResponse(
                alert.getId(),
                alert.getQuery().getId(),
                alert.getProject().getId(),
                alert.getGroup().getId(),
                alert.getGroup().getName(),
                condition,
                alert.isActive(),
                alert.getCreatedAt()
        );
    }
}
