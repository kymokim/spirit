package com.kymokim.spirit.notification.entity;

import com.kymokim.spirit.auth.entity.Auth;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    private Boolean hasRead;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String notificationBody;

    @Embedded
    private RedirectTarget redirectTarget;

    @Builder
    public Notification(Auth auth, NotificationType notificationType, String notificationBody, RedirectTarget redirectTarget) {
        this.auth = auth;
        this.hasRead = false;
        this.notificationType = notificationType;
        this.notificationBody = notificationBody;
        this.redirectTarget = redirectTarget;
    }

    public void readNotification() {
        this.hasRead = true;
    }
}
