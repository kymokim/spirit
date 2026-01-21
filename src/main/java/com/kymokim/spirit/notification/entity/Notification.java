package com.kymokim.spirit.notification.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "notification")
@Entity
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private Long userId;

    private Boolean hasRead;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String notificationBody;

    @Embedded
    private RedirectTarget redirectTarget;

    private String imageUrl;

    @Builder
    public Notification(Long userId, NotificationType notificationType, String notificationBody, RedirectTarget redirectTarget, String imageUrl) {
        this.userId = userId;
        this.hasRead = false;
        this.notificationType = notificationType;
        this.notificationBody = notificationBody;
        this.redirectTarget = redirectTarget;
        this.imageUrl = imageUrl;
    }

    public void readNotification() {
        this.hasRead = true;
    }
}
