package com.onuryigitkocaturk.query_monitor.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Gercek SMTP protokolu ile mail gonderir (JavaMailSender). Su an
 * MailHog'a (Docker, sahte SMTP sunucusu, localhost:8025'te web arayuzu)
 * baglaniyor - gercekten internete/gercek bir mail kutusuna gitmiyor,
 * ama gercek mail gonderim kod yolunu test etmemizi sagliyor.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public NotificationServiceImpl(JavaMailSender mailSender,
                                    @Value("${notification.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendAlertTriggeredEmail(List<String> recipientEmails, String queryName, long matchCount) {
        if (recipientEmails.isEmpty()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipientEmails.toArray(new String[0]));
        message.setSubject("[Query Monitor] Alert tetiklendi: " + queryName);
        message.setText("\"" + queryName + "\" sorgusu " + matchCount
                + " sonuc dondurdu ve tanimli kosulu sagladi.");

        mailSender.send(message);
    }
}
