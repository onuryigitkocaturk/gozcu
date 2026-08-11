package com.onuryigitkocaturk.query_monitor.service.impl;

import com.onuryigitkocaturk.query_monitor.exception.InvalidVerificationCodeException;
import com.onuryigitkocaturk.query_monitor.geocoding.GeocodingService;
import com.onuryigitkocaturk.query_monitor.model.LoginVerification;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.notification.NotificationService;
import com.onuryigitkocaturk.query_monitor.repository.LoginVerificationRepository;
import com.onuryigitkocaturk.query_monitor.service.LoginVerificationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Bilinmeyen bir cihazdan giris yapildiginda devreye giren 6 haneli mail
 * dogrulama kodu akisi. Kod, dusuk entropili (1 milyon ihtimal) oldugu icin
 * PasswordEncoder (BCrypt) ile sifreleniyor - hizli bir hash (SHA-256) burada
 * brute-force'a karsi yetersiz kalirdi.
 */
@Service
public class LoginVerificationServiceImpl implements LoginVerificationService {

    private static final int CODE_EXPIRATION_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    private final LoginVerificationRepository loginVerificationRepository;
    private final NotificationService notificationService;
    private final GeocodingService geocodingService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public LoginVerificationServiceImpl(LoginVerificationRepository loginVerificationRepository,
                                         NotificationService notificationService,
                                         GeocodingService geocodingService,
                                         PasswordEncoder passwordEncoder) {
        this.loginVerificationRepository = loginVerificationRepository;
        this.notificationService = notificationService;
        this.geocodingService = geocodingService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String startVerification(User user, String requestIp, String userAgent, Double latitude, Double longitude) {
        String code = generateSixDigitCode();
        String verificationToken = UUID.randomUUID().toString();

        LoginVerification verification = new LoginVerification(
                user, verificationToken, passwordEncoder.encode(code),
                LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES));
        loginVerificationRepository.save(verification);

        // Nominatim basarisiz olursa (zaman asimi, servis hatasi) locationLabel
        // null kalir - NotificationServiceImpl bu durumda ham koordinatlara doner.
        String locationLabel = (latitude != null && longitude != null)
                ? geocodingService.reverseGeocode(latitude, longitude)
                : null;

        notificationService.sendLoginVerificationCodeEmail(
                user.getEmail(), code, requestIp, userAgent, latitude, longitude, locationLabel);
        return verificationToken;
    }

    @Override
    public User verifyCode(String verificationToken, String code) {
        LoginVerification verification = loginVerificationRepository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new InvalidVerificationCodeException("Doğrulama isteği bulunamadı"));

        if (verification.isUsed()) {
            throw new InvalidVerificationCodeException("Bu kod zaten kullanılmış");
        }
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidVerificationCodeException("Kodun süresi dolmuş, tekrar giriş yapmayı dene");
        }
        if (verification.getAttemptCount() >= MAX_ATTEMPTS) {
            throw new InvalidVerificationCodeException("Çok fazla yanlış deneme, tekrar giriş yapmayı dene");
        }

        if (!passwordEncoder.matches(code, verification.getCodeHash())) {
            verification.setAttemptCount(verification.getAttemptCount() + 1);
            loginVerificationRepository.save(verification);
            throw new InvalidVerificationCodeException("Kod yanlış");
        }

        verification.setUsed(true);
        loginVerificationRepository.save(verification);
        return verification.getUser();
    }

    private String generateSixDigitCode() {
        int value = secureRandom.nextInt(1_000_000);
        return String.format("%06d", value);
    }
}
