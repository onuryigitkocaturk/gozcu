package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.alerting.AlertEvaluationResult;
import com.onuryigitkocaturk.query_monitor.dto.AlertLogResponse;
import com.onuryigitkocaturk.query_monitor.dto.AlertRequest;
import com.onuryigitkocaturk.query_monitor.dto.AlertResponse;
import com.onuryigitkocaturk.query_monitor.mapper.AlertLogMapper;
import com.onuryigitkocaturk.query_monitor.mapper.AlertMapper;
import com.onuryigitkocaturk.query_monitor.model.Alert;
import com.onuryigitkocaturk.query_monitor.service.AlertService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/tables/{tableId}/queries/{queryId}/alerts")
@PreAuthorize("hasRole('ADMIN')")
public class AlertController {

    private static final String AT_LEAST_REPORTER =
            "hasRole('ADMIN') or @projectAuthorizationService.isAtLeastReporter(principal.id, #projectId)";
    private static final String AT_LEAST_DEVELOPER =
            "hasRole('ADMIN') or @projectAuthorizationService.isAtLeastDeveloper(principal.id, #projectId)";
    private static final String AT_LEAST_MAINTAINER =
            "hasRole('ADMIN') or @projectAuthorizationService.isAtLeastMaintainer(principal.id, #projectId)";

    private final AlertService alertService;
    private final AlertMapper alertMapper;
    private final AlertLogMapper alertLogMapper;

    public AlertController(AlertService alertService, AlertMapper alertMapper, AlertLogMapper alertLogMapper) {
        this.alertService = alertService;
        this.alertMapper = alertMapper;
        this.alertLogMapper = alertLogMapper;
    }

    @PreAuthorize(AT_LEAST_DEVELOPER)
    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@PathVariable UUID projectId,
                                                       @PathVariable UUID tableId,
                                                       @PathVariable UUID queryId,
                                                       @Valid @RequestBody AlertRequest request) {
        Alert alert = alertService.createAlert(projectId, queryId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alertMapper.toResponse(alert));
    }

    @PreAuthorize(AT_LEAST_REPORTER)
    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts(@PathVariable UUID projectId,
                                                           @PathVariable UUID tableId,
                                                           @PathVariable UUID queryId) {
        List<Alert> alerts = alertService.getAlertsForQuery(projectId, queryId);
        List<AlertResponse> response = alerts.stream()
                .map(alertMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(AT_LEAST_MAINTAINER)
    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID projectId,
                                              @PathVariable UUID tableId,
                                              @PathVariable UUID queryId,
                                              @PathVariable UUID alertId) {
        alertService.deleteAlert(projectId, alertId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(AT_LEAST_REPORTER)
    @GetMapping("/{alertId}/evaluate")
    public ResponseEntity<AlertEvaluationResult> evaluateAlert(@PathVariable UUID projectId,
                                                                 @PathVariable UUID tableId,
                                                                 @PathVariable UUID queryId,
                                                                 @PathVariable UUID alertId) {
        return ResponseEntity.ok(alertService.evaluateAlert(projectId, alertId));
    }

    @PreAuthorize(AT_LEAST_REPORTER)
    @GetMapping("/{alertId}/logs")
    public ResponseEntity<List<AlertLogResponse>> getAlertLogs(@PathVariable UUID projectId,
                                                                 @PathVariable UUID tableId,
                                                                 @PathVariable UUID queryId,
                                                                 @PathVariable UUID alertId) {
        List<AlertLogResponse> response = alertService.getLogsForAlert(projectId, alertId).stream()
                .map(alertLogMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
