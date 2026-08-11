package com.onuryigitkocaturk.query_monitor.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// gerçek SMTP protokolü ile mail gönderir (JavaMailSender).
@Service
public class NotificationServiceImpl implements NotificationService {

    // mail çok uzun olmasın diye eşleşen satırlardan en fazla bu kadarı listelenir.
    private static final int MAX_ROWS_IN_EMAIL = 20;

    private static final DateTimeFormatter ATTEMPT_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("tr", "TR"));

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

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(recipientEmails.toArray(new String[0]));
            helper.setSubject("[Gözcü] Alert tetiklendi: " + queryName);
            helper.setText(buildHtmlBody(queryName, matchCount, matchedRows), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Alert maili oluşturulamadı", e);
        }
    }

    @Override
    public void sendLoginVerificationCodeEmail(String recipientEmail, String code, String requestIp, String userAgent,
                                                Double latitude, Double longitude, String locationLabel) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(recipientEmail);
            helper.setSubject("[Gözcü] Giriş doğrulama kodun");
            helper.setText(buildVerificationCodeHtmlBody(code, requestIp, userAgent, latitude, longitude, locationLabel), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Doğrulama kodu maili oluşturulamadı", e);
        }
    }

    // Tarayicidan (navigator.geolocation) izinle alinan konum. locationLabel
    // varsa ("Cankaya, Ankara" gibi, GeocodingService ile cozulmus) o gosterilir;
    // geocoding basarisiz olduysa (null) ham koordinatlara geri donulur.
    private String buildLocationLine(Double latitude, Double longitude, String locationLabel) {
        if (latitude == null || longitude == null) {
            return "";
        }
        String displayLocation = (locationLabel != null && !locationLabel.isBlank())
                ? escape(locationLabel)
                : latitude + ", " + longitude;
        return "<p style=\"font-size: 13px; color: #8a94a6; margin: 8px 0 0;\">Yaklaşık konum: <strong>"
                + displayLocation
                + "</strong></p>";
    }

    private String buildVerificationCodeHtmlBody(String code, String requestIp, String userAgent,
                                                  Double latitude, Double longitude, String locationLabel) {
        String displayIp = (requestIp == null || requestIp.isBlank()) ? "bilinmiyor" : escape(requestIp);
        String displayUserAgent = summarizeUserAgent(userAgent);
        String displayAttemptTime = ATTEMPT_TIME_FORMATTER.format(LocalDateTime.now());
        String locationLine = buildLocationLine(latitude, longitude, locationLabel);
        return """
                <div style="font-family: -apple-system, Segoe UI, Arial, sans-serif; max-width: 480px; margin: 0 auto;">
                  <div style="background: #4a90e2; padding: 20px 24px; border-radius: 8px 8px 0 0;">
                    <span style="color: #ffffff; font-size: 20px; font-weight: 700;">gözcü</span>
                  </div>
                  <div style="border: 1px solid #e2e8f0; border-top: none; border-radius: 0 0 8px 8px; padding: 28px; text-align: center;">
                    <p style="font-size: 15px; color: #1a2233; margin: 0 0 20px;">
                      Bilinmeyen bir cihazdan giriş denemesi algılandı. Bu sen değilsen bu kodu kimseyle paylaşma.
                    </p>
                    <div style="font-size: 32px; font-weight: 700; letter-spacing: 6px; color: #4a90e2; ">"""
                + escape(code) +
                """
                    </div>
                    <p style="font-size: 13px; color: #8a94a6; margin: 20px 0 0;">Bu kod 10 dakika içinde geçerliliğini yitirir.</p>
                    <p style="font-size: 13px; color: #8a94a6; margin: 8px 0 0;">Deneme zamanı: <strong>"""
                + escape(displayAttemptTime) +
                """
                    </strong></p>
                    <p style="font-size: 13px; color: #8a94a6; margin: 8px 0 0;">Giriş denemesinin IP adresi: <strong>"""
                + displayIp +
                """
                    </strong></p>
                    <p style="font-size: 13px; color: #8a94a6; margin: 8px 0 0;">Cihaz/tarayıcı: <strong>"""
                + escape(displayUserAgent) +
                """
                    </strong></p>"""
                + locationLine +
                """
                  </div>
                </div>
                """;
    }


    // Ham User-Agent'i genel okunur bir özete indirger.
    private String summarizeUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "bilinmiyor";
        }

        String browser;
        if (userAgent.contains("Edg/")) {
            browser = "Edge";
        } else if (userAgent.contains("OPR/") || userAgent.contains("Opera")) {
            browser = "Opera";
        } else if (userAgent.contains("Firefox/")) {
            browser = "Firefox";
        } else if (userAgent.contains("Chrome/")) {
            browser = "Chrome";
        } else if (userAgent.contains("Safari/")) {
            browser = "Safari";
        } else {
            browser = "bilinmeyen tarayıcı";
        }

        String os;
        if (userAgent.contains("Windows")) {
            os = "Windows";
        } else if (userAgent.contains("Mac OS X")) {
            os = "macOS";
        } else if (userAgent.contains("Android")) {
            os = "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iOS")) {
            os = "iOS";
        } else if (userAgent.contains("Linux")) {
            os = "Linux";
        } else {
            os = "bilinmeyen işletim sistemi";
        }

        return browser + " (" + os + ")";
    }

    private String buildHtmlBody(String queryName, long matchCount, List<Map<String, Object>> matchedRows) {
        StringBuilder html = new StringBuilder();
        html.append("""
                <div style="font-family: -apple-system, Segoe UI, Arial, sans-serif; max-width: 640px; margin: 0 auto;">
                  <div style="background: #4a90e2; padding: 20px 24px; border-radius: 8px 8px 0 0;">
                    <span style="color: #ffffff; font-size: 20px; font-weight: 700;">gözcü</span>
                  </div>
                  <div style="border: 1px solid #e2e8f0; border-top: none; border-radius: 0 0 8px 8px; padding: 24px;">
                """);

        html.append("<p style=\"font-size: 15px; color: #1a2233; margin: 0 0 8px;\">")
                .append("<strong>\"").append(escape(queryName)).append("\"</strong> sorgusu tetiklendi.</p>");
        html.append("<p style=\"font-size: 14px; color: #5a6472; margin: 0 0 20px;\">")
                .append("Eşleşen satır sayısı: <strong>").append(matchCount).append("</strong></p>");

        if (!matchedRows.isEmpty()) {
            int shown = Math.min(matchedRows.size(), MAX_ROWS_IN_EMAIL);
            Set<String> columns = new LinkedHashSet<>();
            for (int i = 0; i < shown; i++) {
                columns.addAll(matchedRows.get(i).keySet());
            }

            html.append("<table style=\"width: 100%; border-collapse: collapse; font-size: 13px; ")
                    .append("border: 1px solid #dce3ea; border-radius: 6px; overflow: hidden;\">");
            html.append("<thead><tr>");
            for (String column : columns) {
                html.append("<th style=\"text-align: left; padding: 10px 12px; background: #4a90e2; ")
                        .append("color: #ffffff; border: 1px solid #3a78c2; white-space: nowrap;\">")
                        .append(escape(column)).append("</th>");
            }
            html.append("</tr></thead><tbody>");

            for (int i = 0; i < shown; i++) {
                Map<String, Object> row = matchedRows.get(i);
                String rowBackground = i % 2 == 0 ? "#ffffff" : "#f7fafd";
                html.append("<tr style=\"background: ").append(rowBackground).append(";\">");
                for (String column : columns) {
                    Object value = row.get(column);
                    html.append("<td style=\"padding: 8px 12px; border: 1px solid #eaeef2; color: #1a2233; white-space: nowrap;\">")
                            .append(value == null ? "—" : escape(String.valueOf(value)))
                            .append("</td>");
                }
                html.append("</tr>");
            }
            html.append("</tbody></table>");

            if (matchedRows.size() > shown) {
                html.append("<p style=\"font-size: 13px; color: #8a94a6; margin: 12px 0 0;\">… ve ")
                        .append(matchedRows.size() - shown).append(" kayıt daha.</p>");
            }
        }

        html.append("</div></div>");
        return html.toString();
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
