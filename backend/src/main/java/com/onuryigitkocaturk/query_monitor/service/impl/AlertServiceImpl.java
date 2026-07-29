package com.onuryigitkocaturk.query_monitor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onuryigitkocaturk.query_monitor.alerting.AlertEvaluationResult;
import com.onuryigitkocaturk.query_monitor.alerting.AlertEvaluationService;
import com.onuryigitkocaturk.query_monitor.dto.AlertConditionValue;
import com.onuryigitkocaturk.query_monitor.dto.AlertRequest;
import com.onuryigitkocaturk.query_monitor.enums.ConditionOperator;
import com.onuryigitkocaturk.query_monitor.exception.AlertNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.GroupNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.InvalidAlertConditionException;
import com.onuryigitkocaturk.query_monitor.exception.ProjectNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.QueryNotFoundException;
import com.onuryigitkocaturk.query_monitor.model.Alert;
import com.onuryigitkocaturk.query_monitor.model.AlertLog;
import com.onuryigitkocaturk.query_monitor.model.Group;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.repository.AlertLogRepository;
import com.onuryigitkocaturk.query_monitor.repository.AlertRepository;
import com.onuryigitkocaturk.query_monitor.repository.GroupRepository;
import com.onuryigitkocaturk.query_monitor.repository.ProjectRepository;
import com.onuryigitkocaturk.query_monitor.repository.QueryRepository;
import com.onuryigitkocaturk.query_monitor.service.AlertService;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AlertServiceImpl implements AlertService {

    private static final Set<ConditionOperator> ALLOWED_ALERT_OPERATORS = EnumSet.of(
            ConditionOperator.EQUALS, ConditionOperator.NOT_EQUALS,
            ConditionOperator.GREATER_THAN, ConditionOperator.GREATER_THAN_OR_EQUAL,
            ConditionOperator.LESS_THAN, ConditionOperator.LESS_THAN_OR_EQUAL
    );

    private final AlertRepository alertRepository;
    private final AlertLogRepository alertLogRepository;
    private final ProjectRepository projectRepository;
    private final QueryRepository queryRepository;
    private final GroupRepository groupRepository;
    private final AlertEvaluationService alertEvaluationService;
    private final ObjectMapper objectMapper;

    public AlertServiceImpl(AlertRepository alertRepository,
                             AlertLogRepository alertLogRepository,
                             ProjectRepository projectRepository,
                             QueryRepository queryRepository,
                             GroupRepository groupRepository,
                             AlertEvaluationService alertEvaluationService,
                             ObjectMapper objectMapper) {
        this.alertRepository = alertRepository;
        this.alertLogRepository = alertLogRepository;
        this.projectRepository = projectRepository;
        this.queryRepository = queryRepository;
        this.groupRepository = groupRepository;
        this.alertEvaluationService = alertEvaluationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Alert createAlert(UUID projectId, UUID queryId, AlertRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));

        com.onuryigitkocaturk.query_monitor.model.Query query = queryRepository.findById(queryId)
                .orElseThrow(() -> new QueryNotFoundException("Sorgu bulunamadı: " + queryId));
        if (!query.getProject().getId().equals(projectId)) {
            throw new QueryNotFoundException("Sorgu bulunamadı: " + queryId);
        }

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException("Grup bulunamadı: " + request.getGroupId()));

        validateCondition(request.getCondition());

        String conditionJson;
        try {
            conditionJson = objectMapper.writeValueAsString(request.getCondition());
        } catch (JsonProcessingException e) {
            throw new InvalidAlertConditionException("Alert koşulu işlenemedi");
        }

        Alert alert = new Alert(conditionJson, query, project, group);
        alert.setActive(true);
        return alertRepository.save(alert);
    }

    @Override
    public void deleteAlert(UUID projectId, UUID alertId) {
        alertRepository.delete(getValidatedAlert(projectId, alertId));
    }

    @Override
    public List<Alert> getAlertsForQuery(UUID projectId, UUID queryId) {
        com.onuryigitkocaturk.query_monitor.model.Query query = queryRepository.findById(queryId)
                .orElseThrow(() -> new QueryNotFoundException("Sorgu bulunamadı: " + queryId));
        if (!query.getProject().getId().equals(projectId)) {
            throw new QueryNotFoundException("Sorgu bulunamadı: " + queryId);
        }
        return alertRepository.findByQueryId(queryId);
    }

    @Override
    public AlertEvaluationResult evaluateAlert(UUID projectId, UUID alertId) {
        Alert alert = getValidatedAlert(projectId, alertId);
        return alertEvaluationService.evaluate(
                alert.getQuery().getProjectTable().getTableName(),
                alert.getQuery().getDefinitionJson(),
                alert.getConditionExpression());
    }

    @Override
    public List<AlertLog> getLogsForAlert(UUID projectId, UUID alertId) {
        Alert alert = getValidatedAlert(projectId, alertId);
        return alertLogRepository.findByAlertId(alert.getId());
    }

    private void validateCondition(AlertConditionValue condition) {
        if (!ALLOWED_ALERT_OPERATORS.contains(condition.getOperator())) {
            throw new InvalidAlertConditionException(
                    "Alert koşulu için desteklenmeyen işleç: " + condition.getOperator());
        }
    }

    private Alert getValidatedAlert(UUID projectId, UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert bulunamadı: " + alertId));

        if (!alert.getProject().getId().equals(projectId)) {
            throw new AlertNotFoundException("Alert bulunamadı: " + alertId);
        }

        return alert;
    }
}
