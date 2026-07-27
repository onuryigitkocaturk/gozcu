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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/tables/{tableId}/queries/{queryId}/alerts")
@PreAuthorize("hasRole('ADMIN')")
public class AlertController {

    private final AlertService alertService;
    private final AlertMapper alertMapper;
    private final AlertLogMapper alertLogMapper;

    public AlertController(AlertService alertService, AlertMapper alertMapper, AlertLogMapper alertLogMapper) {
        this.alertService = alertService;
        this.alertMapper = alertMapper;
        this.alertLogMapper = alertLogMapper;
    }

    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@PathVariable Long projectId,
                                                       @PathVariable Long tableId,
                                                       @PathVariable Long queryId,
                                                       @Valid @RequestBody AlertRequest request) {
        Alert alert = alertService.createAlert(projectId, queryId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alertMapper.toResponse(alert));
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts(@PathVariable Long projectId,
                                                           @PathVariable Long tableId,
                                                           @PathVariable Long queryId) {
        List<Alert> alerts = alertService.getAlertsForQuery(projectId, queryId);
        List<AlertResponse> response = alerts.stream()
                .map(alertMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long projectId,
                                              @PathVariable Long tableId,
                                              @PathVariable Long queryId,
                                              @PathVariable Long alertId) {
        alertService.deleteAlert(projectId, alertId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping("/{alertId}/evaluate")
    public ResponseEntity<AlertEvaluationResult> evaluateAlert(@PathVariable Long projectId,
                                                                 @PathVariable Long tableId,
                                                                 @PathVariable Long queryId,
                                                                 @PathVariable Long alertId) {
        return ResponseEntity.ok(alertService.evaluateAlert(projectId, alertId));
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping("/{alertId}/logs")
    public ResponseEntity<List<AlertLogResponse>> getAlertLogs(@PathVariable Long projectId,
                                                                 @PathVariable Long tableId,
                                                                 @PathVariable Long queryId,
                                                                 @PathVariable Long alertId) {
        List<AlertLogResponse> response = alertService.getLogsForAlert(projectId, alertId).stream()
                .map(alertLogMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
