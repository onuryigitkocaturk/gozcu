package com.onuryigitkocaturk.query_monitor.scheduler;

import com.onuryigitkocaturk.query_monitor.alerting.AlertEvaluationResult;
import com.onuryigitkocaturk.query_monitor.alerting.AlertEvaluationService;
import com.onuryigitkocaturk.query_monitor.connector.ConnectionCredentialEncryptor;
import com.onuryigitkocaturk.query_monitor.connector.ConnectionDetails;
import com.onuryigitkocaturk.query_monitor.connector.QueryExecutionService;
import com.onuryigitkocaturk.query_monitor.enums.Frequency;
import com.onuryigitkocaturk.query_monitor.enums.LogStatus;
import com.onuryigitkocaturk.query_monitor.model.Alert;
import com.onuryigitkocaturk.query_monitor.model.AlertLog;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.Query;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.notification.NotificationService;
import com.onuryigitkocaturk.query_monitor.repository.AlertLogRepository;
import com.onuryigitkocaturk.query_monitor.repository.AlertRepository;
import com.onuryigitkocaturk.query_monitor.repository.QueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

// Aktif query'leri kendi frequency'sine göre periyodik olarak değerlendirir
// tetiklenirse Group'un üyelerine mail atar (NotificiationService üzerinden, MailHog'a - internete gitmez)
// ve sonucu AlertLog'a yazar.
@Component
public class AlertScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertScheduler.class);

    private final QueryRepository queryRepository;
    private final AlertRepository alertRepository;
    private final AlertLogRepository alertLogRepository;
    private final AlertEvaluationService alertEvaluationService;
    private final QueryExecutionService queryExecutionService;
    private final NotificationService notificationService;
    private final ConnectionCredentialEncryptor connectionCredentialEncryptor;

    public AlertScheduler(QueryRepository queryRepository,
                           AlertRepository alertRepository,
                           AlertLogRepository alertLogRepository,
                           AlertEvaluationService alertEvaluationService,
                           QueryExecutionService queryExecutionService,
                           NotificationService notificationService,
                           ConnectionCredentialEncryptor connectionCredentialEncryptor) {
        this.queryRepository = queryRepository;
        this.alertRepository = alertRepository;
        this.alertLogRepository = alertLogRepository;
        this.alertEvaluationService = alertEvaluationService;
        this.queryExecutionService = queryExecutionService;
        this.notificationService = notificationService;
        this.connectionCredentialEncryptor = connectionCredentialEncryptor;
    }

    @Transactional
    @Scheduled(fixedRateString = "${scheduler.hourly-rate-ms:3600000}")
    public void runHourlyQueries() {
        evaluateQueriesWithFrequency(Frequency.HOURLY);
    }

    @Transactional
    @Scheduled(fixedRateString = "${scheduler.daily-rate-ms:86400000}")
    public void runDailyQueries() {
        evaluateQueriesWithFrequency(Frequency.DAILY);
    }

    private void evaluateQueriesWithFrequency(Frequency frequency) {
        List<Query> queries = queryRepository.findByActiveTrue().stream()
                .filter(query -> query.getFrequency() == frequency)
                .toList();

        log.info("Scheduler calisti: frequency={}, aktif query sayisi={}", frequency, queries.size());

        for (Query query : queries) {
            List<Alert> alerts = alertRepository.findByQueryId(query.getId()).stream()
                    .filter(Alert::isActive)
                    .toList();

            for (Alert alert : alerts) {
                evaluateAndLog(alert);
            }
        }
    }

    private void evaluateAndLog(Alert alert) {
        LogStatus status;
        String message;

        try {
            ConnectionDetails connection = toConnectionDetails(alert.getProject());
            AlertEvaluationResult result = alertEvaluationService.evaluate(
                    connection,
                    alert.getQuery().getProjectTable().getTableName(),
                    alert.getQuery().getDefinitionJson(),
                    alert.getConditionExpression());

            if (result.triggered()) {
                status = LogStatus.TRIGGERED;
                List<String> recipientEmails = alert.getGroup().getUsers().stream()
                        .map(User::getEmail)
                        .toList();
                List<Map<String, Object>> matchedRows = queryExecutionService.executeQuery(
                        connection,
                        alert.getQuery().getProjectTable().getTableName(),
                        alert.getQuery().getDefinitionJson());
                notificationService.sendAlertTriggeredEmail(
                        recipientEmails, alert.getQuery().getName(), result.matchCount(), matchedRows);
                message = "Eslesen satir sayisi: " + result.matchCount() + ". "
                        + recipientEmails.size() + " kisiye mail gonderildi.";
            } else {
                status = LogStatus.NOT_TRIGGERED;
                message = "Eslesen satir sayisi: " + result.matchCount() + ". Kosul saglanmadi.";
            }
        } catch (Exception e) {
            status = LogStatus.ERROR;
            message = "Degerlendirme basarisiz: " + e.getMessage();
            log.error("Alert degerlendirilirken hata olustu, alertId={}", alert.getId(), e);
        }

        alertLogRepository.save(new AlertLog(alert, status, message));
    }

    private ConnectionDetails toConnectionDetails(Project project) {
        return new ConnectionDetails(
                project.getDbHost(),
                project.getDbPort(),
                project.getDbName(),
                project.getDbUsername(),
                connectionCredentialEncryptor.decrypt(project.getDbPasswordEncrypted()));
    }
}
