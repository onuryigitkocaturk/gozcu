package com.onuryigitkocaturk.query_monitor.repository;

import com.onuryigitkocaturk.query_monitor.model.TrustedDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, UUID> {

    boolean existsByUserIdAndDeviceTokenHashAndUserAgentHash(
            UUID userId, String deviceTokenHash, String userAgentHash);

    List<TrustedDevice> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Ayni tarayici imzasi (User-Agent hash'i) icin zaten bir kayit varsa,
    // yeniden dogrulamada YENI satir acmak yerine bu kaydi guncellemek icin.
    Optional<TrustedDevice> findByUserIdAndUserAgentHash(UUID userId, String userAgentHash);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);
}
