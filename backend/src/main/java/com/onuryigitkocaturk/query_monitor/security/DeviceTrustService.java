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

/**
 * Tarayicidaki HttpOnly "device_token" cookie'sini yonetir - JWT'den tamamen
 * ayri bir mekanizma. Cookie'nin kendisi rastgele, yuksek entropili (256 bit)
 * bir deger oldugu icin BCrypt gibi yavas bir hash'e gerek yok; SHA-256 (hizli,
 * deterministik) yeterli - brute-force zaten pratik degil (2^256 ihtimal).
 */
@Component
public class DeviceTrustService {

    private final TrustedDeviceRepository trustedDeviceRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public DeviceTrustService(TrustedDeviceRepository trustedDeviceRepository) {
        this.trustedDeviceRepository = trustedDeviceRepository;
    }

    public boolean isTrusted(UUID userId, String rawDeviceToken) {
        if (rawDeviceToken == null || rawDeviceToken.isBlank()) {
            return false;
        }
        return trustedDeviceRepository.existsByUserIdAndDeviceTokenHash(userId, hash(rawDeviceToken));
    }

    /** Yeni bir cihazi guvenilir olarak isaretler, tarayiciya konacak HAM token'i doner. */
    public String trustNewDevice(User user) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rawDeviceToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        trustedDeviceRepository.save(new TrustedDevice(user, hash(rawDeviceToken)));
        return rawDeviceToken;
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
