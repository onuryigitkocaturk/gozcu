package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.alerting.AlertEvaluationResult;
import com.onuryigitkocaturk.query_monitor.dto.AlertRequest;
import com.onuryigitkocaturk.query_monitor.model.Alert;
import com.onuryigitkocaturk.query_monitor.model.AlertLog;

import java.util.List;
import java.util.UUID;

public interface AlertService {

    Alert createAlert(UUID projectId, UUID queryId, AlertRequest request);

    void deleteAlert(UUID projectId, UUID alertId);

    List<Alert> getAlertsForQuery(UUID projectId, UUID queryId);

    AlertEvaluationResult evaluateAlert(UUID projectId, UUID alertId);

    List<AlertLog> getLogsForAlert(UUID projectId, UUID alertId);
}
