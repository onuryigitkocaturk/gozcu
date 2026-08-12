package com.onuryigitkocaturk.query_monitor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

// kullanıcının doğruladığı cihazı temsil eder.
@Entity
@Table(name = "trusted_devices")
public class TrustedDevice {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_token_hash", nullable = false)
    private String deviceTokenHash;

    @Column(name = "user_agent_hash", nullable = false)
    private String userAgentHash;

    // guvenlik kontrolu icin degil, sadece "Hesabim" sayfasinda okunakli
    // gosterim icin - UserAgentSummarizer ile hesaplanir, hash'lenmez.
    @Column(name = "browser_label")
    private String browserLabel;

    // GeocodingService ile cozulmus konum, LoginVerification'dan tasinir.
    @Column(name = "location_label")
    private String locationLabel;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TrustedDevice() {
    }

    public TrustedDevice(User user, String deviceTokenHash, String userAgentHash,
                          String browserLabel, String locationLabel) {
        this.user = user;
        this.deviceTokenHash = deviceTokenHash;
        this.userAgentHash = userAgentHash;
        this.browserLabel = browserLabel;
        this.locationLabel = locationLabel;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDeviceTokenHash() {
        return deviceTokenHash;
    }

    public void setDeviceTokenHash(String deviceTokenHash) {
        this.deviceTokenHash = deviceTokenHash;
    }

    public String getUserAgentHash() {
        return userAgentHash;
    }

    public void setUserAgentHash(String userAgentHash) {
        this.userAgentHash = userAgentHash;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrustedDevice)) return false;
        TrustedDevice that = (TrustedDevice) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
