package com.kymokim.spirit.auth.repository;

import com.kymokim.spirit.auth.entity.NotificationConsent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationConsentRepository extends JpaRepository<NotificationConsent, Long> {
}
