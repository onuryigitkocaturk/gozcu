package com.onuryigitkocaturk.query_monitor.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Gercek SMTP protokolu ile mail gonderir (JavaMailSender). Su an
 * MailHog'a (Docker, sahte SMTP sunucusu, localhost:8025'te web arayuzu)
 * baglaniyor - gercekten internete/gercek bir mail kutusuna gitmiyor,
 * ama gercek mail gonderim kod yolunu test etmemizi sagliyor.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    /** Mail cok uzun olmasin diye eslesen satirlardan en fazla bu kadari listelenir. */
    private static final int MAX_ROWS_IN_EMAIL = 20;

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public NotificationServiceImpl(JavaMailSender mailSender,
                                    @Value("${notification.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendAlertTriggeredEmail(List<String> recipientEmails, String queryName, long matchCount,
                                         List<Map<String, Object>> matchedRows) {
        if (recipientEmails.isEmpty()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipientEmails.toArray(new String[0]));
        message.setSubject("[Query Monitor] Alert tetiklendi: " + queryName);
        message.setText(buildBody(queryName, matchCount, matchedRows));

        mailSender.send(message);
    }

    private String buildBody(String queryName, long matchCount, List<Map<String, Object>> matchedRows) {
        StringBuilder body = new StringBuilder();
        body.append("\"").append(queryName).append("\" sorgusu ").append(matchCount)
                .append(" sonuc dondurdu ve tanimli kosulu sagladi.\n");

        if (!matchedRows.isEmpty()) {
            int shown = Math.min(matchedRows.size(), MAX_ROWS_IN_EMAIL);
            body.append("\nEslesen kayitlar:\n");
            for (int i = 0; i < shown; i++) {
                body.append(i + 1).append(". ").append(matchedRows.get(i)).append("\n");
            }
            if (matchedRows.size() > shown) {
                body.append("... ve ").append(matchedRows.size() - shown).append(" kayit daha.\n");
            }
        }

        return body.toString();
    }
}
