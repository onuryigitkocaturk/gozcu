package com.onuryigitkocaturk.query_monitor.notification;

import java.util.List;

public interface NotificationService {

    void sendAlertTriggeredEmail(List<String> recipientEmails, String queryName, long matchCount);
}
