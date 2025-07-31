package com.kymokim.spirit.auth.auth.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "notification_consent")
@Entity
@Getter
@NoArgsConstructor
public class NotificationConsent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    private Boolean pushConsent;

    private Boolean marketingConsent;

    private LocalDateTime marketingAgreedAt;

    private LocalDateTime marketingWithdrawnAt;

    @Builder
    public NotificationConsent(Auth auth) {
        this.auth = auth;
    }

    public void updatePushConsent(Boolean pushConsent) {
        this.pushConsent = pushConsent;
    }

    public void agreeMarketingConsent() {
        this.marketingConsent = true;
        this.marketingAgreedAt = LocalDateTime.now();
    }

    public void withdrawMarketingConsent() {
        this.marketingConsent = false;
        this.marketingWithdrawnAt = LocalDateTime.now();
    }
}
