package com.onuryigitkocaturk.query_monitor.security;

import com.onuryigitkocaturk.query_monitor.model.TrustedDevice;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.repository.TrustedDeviceRepository;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

// tarayıcıdaki HttpOnly "device_token" cookie'sini yönetir.
@Component
public class DeviceTrustService {

    private final TrustedDeviceRepository trustedDeviceRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public DeviceTrustService(TrustedDeviceRepository trustedDeviceRepository) {
        this.trustedDeviceRepository = trustedDeviceRepository;
    }

    public boolean isTrusted(UUID userId, String rawDeviceToken, String userAgent) {
        if (rawDeviceToken == null || rawDeviceToken.isBlank()) {
            return false;
        }
        return trustedDeviceRepository.existsByUserIdAndDeviceTokenHashAndUserAgentHash(
                userId, hash(rawDeviceToken), hash(normalize(userAgent)));
    }

    // yeni cihazı güvenilir olarak işaretler, tarayıcıya ham token'ı döner.
    public String trustNewDevice(User user, String userAgent) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rawDeviceToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        trustedDeviceRepository.save(new TrustedDevice(user, hash(rawDeviceToken), hash(normalize(userAgent))));
        return rawDeviceToken;
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? "bilinmiyor" : value;
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algoritması bulunamadı", e);
        }
    }
}
