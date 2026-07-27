package com.onuryigitkocaturk.query_monitor.mapper;

import com.onuryigitkocaturk.query_monitor.dto.AlertLogResponse;
import com.onuryigitkocaturk.query_monitor.model.AlertLog;
import org.springframework.stereotype.Component;

@Component
public class AlertLogMapper {

    public AlertLogResponse toResponse(AlertLog alertLog) {
        return new AlertLogResponse(
                alertLog.getId(),
                alertLog.getStatus(),
                alertLog.getMessage(),
                alertLog.getExecutedAt()
        );
    }
}
