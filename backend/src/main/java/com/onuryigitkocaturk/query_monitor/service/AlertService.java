package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.alerting.AlertEvaluationResult;
import com.onuryigitkocaturk.query_monitor.dto.AlertRequest;
import com.onuryigitkocaturk.query_monitor.model.Alert;

import java.util.List;

public interface AlertService {

    Alert createAlert(Long projectId, Long queryId, AlertRequest request);

    void deleteAlert(Long projectId, Long alertId);

    List<Alert> getAlertsForQuery(Long projectId, Long queryId);

    AlertEvaluationResult evaluateAlert(Long projectId, Long alertId);
}
