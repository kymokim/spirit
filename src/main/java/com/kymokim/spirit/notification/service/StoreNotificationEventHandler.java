package com.kymokim.spirit.notification.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.notification.dto.store.StoreOwnershipApprovedNotificationEvent;
import com.kymokim.spirit.notification.dto.store.StoreOwnershipRejectedNotificationEvent;
import com.kymokim.spirit.notification.entity.Notification;
import com.kymokim.spirit.notification.entity.NotificationType;
import com.kymokim.spirit.notification.entity.RedirectTarget;
import com.kymokim.spirit.notification.entity.RedirectType;
import com.kymokim.spirit.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoreNotificationEventHandler {
    private final FCMNotificationService fcmNotificationService;
    private final NotificationRepository notificationRepository;

    @EventListener(StoreOwnershipApprovedNotificationEvent.class)
    public void handle(StoreOwnershipApprovedNotificationEvent event) {
        Auth user = event.getUser();
        NotificationType notificationType = NotificationType.STORE_OWNERSHIP_APPROVED;
        RedirectTarget redirectTarget = RedirectTarget.builder().redirectType(RedirectType.STORE_DETAIL).redirectId(event.getStore().getId()).build();
        String body = notificationType.format(Map.of(
                "storeName", event.getStore().getName()
        ));
        Notification notification = Notification.builder()
                .auth(user)
                .notificationType(notificationType)
                .notificationBody(body)
                .redirectTarget(redirectTarget)
                .build();
        notification = notificationRepository.save(notification);
        if (Boolean.TRUE.equals(user.getNotificationConsent().getPushConsent())){
            fcmNotificationService.pushAlarmToToken(notification);
        }
    }

    @EventListener(StoreOwnershipRejectedNotificationEvent.class)
    public void handle(StoreOwnershipRejectedNotificationEvent event) {
        Auth user = event.getUser();
        NotificationType notificationType = NotificationType.STORE_OWNERSHIP_REJECTED;
        RedirectTarget redirectTarget = RedirectTarget.builder().redirectType(RedirectType.OWNERSHIP_REJECTION).redirectId(0L).build();
        String body = notificationType.format(Map.of(
                "storeName", event.getStore().getName(),
                "rejectionReason", event.getRejectionReason()
        ));
        Notification notification = Notification.builder()
                .auth(user)
                .notificationType(notificationType)
                .notificationBody(body)
                .redirectTarget(redirectTarget)
                .build();
        notification = notificationRepository.save(notification);
        if (Boolean.TRUE.equals(user.getNotificationConsent().getPushConsent())){
            fcmNotificationService.pushAlarmToToken(notification);
        }
    }
}
