package com.onuryigitkocaturk.query_monitor.notification;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    void sendAlertTriggeredEmail(List<String> recipientEmails, String queryName, long matchCount,
                                  List<Map<String, Object>> matchedRows);

    void sendLoginVerificationCodeEmail(String recipientEmail, String code, String requestIp, String userAgent,
                                         Double latitude, Double longitude, String locationLabel);
}
