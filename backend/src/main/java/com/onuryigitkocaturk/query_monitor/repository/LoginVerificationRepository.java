package com.onuryigitkocaturk.query_monitor.repository;

import com.onuryigitkocaturk.query_monitor.model.LoginVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoginVerificationRepository extends JpaRepository<LoginVerification, UUID> {

    Optional<LoginVerification> findByVerificationToken(String verificationToken);

    void deleteByUserId(UUID userId);
}
