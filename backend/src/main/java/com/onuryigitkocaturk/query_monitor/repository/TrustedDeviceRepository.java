package com.onuryigitkocaturk.query_monitor.repository;

import com.onuryigitkocaturk.query_monitor.model.TrustedDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, UUID> {

    boolean existsByUserIdAndDeviceTokenHash(UUID userId, String deviceTokenHash);
}
