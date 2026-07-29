package com.onuryigitkocaturk.query_monitor.dto;

import com.onuryigitkocaturk.query_monitor.enums.LogStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlertLogResponse {

    private UUID id;
    private LogStatus status;
    private String message;
    private LocalDateTime executedAt;

    public AlertLogResponse() {
    }

    public AlertLogResponse(UUID id, LogStatus status, String message, LocalDateTime executedAt) {
        this.id = id;
        this.status = status;
        this.message = message;
        this.executedAt = executedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LogStatus getStatus() {
        return status;
    }

    public void setStatus(LogStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
}
