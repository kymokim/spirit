package com.kymokim.spirit.notification.service;

import com.kymokim.spirit.notification.dto.store.StoreOwnershipApprovedNotificationEvent;
import com.kymokim.spirit.notification.dto.store.StoreOwnershipRejectedNotificationEvent;
import com.kymokim.spirit.notification.entity.NotificationType;
import com.kymokim.spirit.notification.entity.RedirectTarget;
import com.kymokim.spirit.notification.entity.RedirectType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StoreNotificationEventHandler {
    private final FCMNotificationService fcmNotificationService;

    @EventListener(StoreOwnershipApprovedNotificationEvent.class)
    public void handle(StoreOwnershipApprovedNotificationEvent event) {
        NotificationType notificationType = NotificationType.STORE_OWNERSHIP_APPROVED;
        RedirectTarget redirectTarget = RedirectTarget.builder().redirectType(RedirectType.STORE_DETAIL).redirectId(event.getStore().getId()).build();
        String body = notificationType.format(Map.of(
                "storeName", event.getStore().getName()
        ));
        if (!Objects.equals(event.getUser().getFcmToken(), null)){
            fcmNotificationService.pushAlarmToToken(notificationType, body, event.getUser().getFcmToken(), redirectTarget);
        }
    }

    @EventListener(StoreOwnershipRejectedNotificationEvent.class)
    public void handle(StoreOwnershipRejectedNotificationEvent event) {
        NotificationType notificationType = NotificationType.STORE_OWNERSHIP_REJECTED;
        RedirectTarget redirectTarget = RedirectTarget.builder().redirectType(RedirectType.STORE_DETAIL).redirectId(event.getStore().getId()).build();
        String body = notificationType.format(Map.of(
                "storeName", event.getStore().getName(),
                "rejectionReason", event.getRejectionReason()
        ));
        if (!Objects.equals(event.getUser().getFcmToken(), null)){
            fcmNotificationService.pushAlarmToToken(notificationType, body, event.getUser().getFcmToken(), redirectTarget);
        }
    }
}
