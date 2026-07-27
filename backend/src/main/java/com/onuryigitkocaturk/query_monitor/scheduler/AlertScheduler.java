package com.onuryigitkocaturk.query_monitor.scheduler;

import com.onuryigitkocaturk.query_monitor.alerting.AlertEvaluationResult;
import com.onuryigitkocaturk.query_monitor.alerting.AlertEvaluationService;
import com.onuryigitkocaturk.query_monitor.enums.Frequency;
import com.onuryigitkocaturk.query_monitor.enums.LogStatus;
import com.onuryigitkocaturk.query_monitor.model.Alert;
import com.onuryigitkocaturk.query_monitor.model.AlertLog;
import com.onuryigitkocaturk.query_monitor.model.Query;
import com.onuryigitkocaturk.query_monitor.repository.AlertLogRepository;
import com.onuryigitkocaturk.query_monitor.repository.AlertRepository;
import com.onuryigitkocaturk.query_monitor.repository.QueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Aktif Query'leri kendi frequency'sine gore periyodik olarak degerlendirir
 * ve sonucu AlertLog'a yazar. MAIL GONDERMEZ HENUZ - notification/ paketi
 * (MailHog ile) ayri bir artista eklenecek; su an sadece "tetiklendi mi"
 * bilgisi kalici olarak loglaniyor.
 *
 * Cron ifadeleri application.properties'ten override edilebilir - gercek
 * saatlik/gunluk donguyu beklemeden hizli test yapabilmek icin.
 */
@Component
public class AlertScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertScheduler.class);

    private final QueryRepository queryRepository;
    private final AlertRepository alertRepository;
    private final AlertLogRepository alertLogRepository;
    private final AlertEvaluationService alertEvaluationService;

    public AlertScheduler(QueryRepository queryRepository,
                           AlertRepository alertRepository,
                           AlertLogRepository alertLogRepository,
                           AlertEvaluationService alertEvaluationService) {
        this.queryRepository = queryRepository;
        this.alertRepository = alertRepository;
        this.alertLogRepository = alertLogRepository;
        this.alertEvaluationService = alertEvaluationService;
    }

    @Transactional
    @Scheduled(cron = "${scheduler.hourly-cron:0 0 * * * *}")
    public void runHourlyQueries() {
        evaluateQueriesWithFrequency(Frequency.HOURLY);
    }

    @Transactional
    @Scheduled(cron = "${scheduler.daily-cron:0 0 0 * * *}")
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
            AlertEvaluationResult result = alertEvaluationService.evaluate(
                    alert.getQuery().getProjectTable().getTableName(),
                    alert.getQuery().getDefinitionJson(),
                    alert.getConditionExpression());

            if (result.triggered()) {
                status = LogStatus.TRIGGERED;
                // TODO: notification/ paketi eklenince burada alert.getGroup()'a mail gonderilecek.
                message = "Eslesen satir sayisi: " + result.matchCount() + ". Mail gonderimi henuz eklenmedi.";
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
}
