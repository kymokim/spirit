package com.kymokim.spirit.notification.dto;

import com.kymokim.spirit.notification.entity.Notification;
import com.kymokim.spirit.notification.entity.NotificationType;
import com.kymokim.spirit.notification.entity.RedirectType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ResponseNotification {
    @Getter
    @Builder
    public static class NotificationResponseDto {
        private Long id;
        private LocalDateTime createdAt;
        private Boolean hasRead;
        private NotificationType notificationType;
        private String title;
        private String body;
        private RedirectType redirectType;
        private Long redirectId;
        private String imageUrl;

        public static NotificationResponseDto toDto(Notification notification) {
            return NotificationResponseDto.builder()
                    .id(notification.getId())
                    .createdAt(notification.getCreatedAt())
                    .hasRead(notification.getHasRead())
                    .notificationType(notification.getNotificationType())
                    .title(notification.getNotificationType().getTitle())
                    .body(notification.getNotificationBody())
                    .redirectType(notification.getRedirectTarget().getRedirectType())
                    .redirectId(notification.getRedirectTarget().getRedirectId())
                    .imageUrl(notification.getImageUrl() == null ? null : notification.getImageUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class HasUnreadResponseDto {
        private boolean hasUnread;

        public static HasUnreadResponseDto toDto(boolean hasUnread) {
            return HasUnreadResponseDto.builder()
                    .hasUnread(hasUnread)
                    .build();
        }
    }
}
