package com.onuryigitkocaturk.query_monitor.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TrustedDeviceResponse {

    private UUID id;
    private String browserLabel;
    private String locationLabel;
    private LocalDateTime createdAt;

    public TrustedDeviceResponse() {
    }

    public TrustedDeviceResponse(UUID id, String browserLabel, String locationLabel, LocalDateTime createdAt) {
        this.id = id;
        this.browserLabel = browserLabel;
        this.locationLabel = locationLabel;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBrowserLabel() {
        return browserLabel;
    }

    public void setBrowserLabel(String browserLabel) {
        this.browserLabel = browserLabel;
    }

    public String getLocationLabel() {
        return locationLabel;
    }

    public void setLocationLabel(String locationLabel) {
        this.locationLabel = locationLabel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
