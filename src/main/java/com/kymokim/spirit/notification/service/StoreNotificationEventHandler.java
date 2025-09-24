package com.kymokim.spirit.notification.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.notification.dto.store.*;
import com.kymokim.spirit.notification.entity.Notification;
import com.kymokim.spirit.notification.entity.NotificationType;
import com.kymokim.spirit.notification.entity.RedirectTarget;
import com.kymokim.spirit.notification.entity.RedirectType;
import com.kymokim.spirit.notification.repository.NotificationRepository;
import com.kymokim.spirit.store.entity.StoreManager;
import com.kymokim.spirit.store.repository.StoreManagerRepository;
import com.kymokim.spirit.auth.service.AuthResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@MainTransactional
public class StoreNotificationEventHandler {
    private final FCMNotificationService fcmNotificationService;
    private final NotificationRepository notificationRepository;
    private final StoreManagerRepository storeManagerRepository;

    @EventListener(StoreOwnershipApprovedNotificationEvent.class)
    public void handle(StoreOwnershipApprovedNotificationEvent event) {
        Auth user = event.getUser();
        NotificationType notificationType = NotificationType.STORE_OWNERSHIP_APPROVED;
        RedirectTarget redirectTarget = RedirectTarget.builder().redirectType(RedirectType.STORE_DETAIL).redirectId(event.getStore().getId()).build();
        String body = notificationType.format(Map.of(
                "storeName", event.getStore().getName()
        ));
        Notification notification = Notification.builder()
                .userId(user.getId())
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
                .userId(user.getId())
                .notificationType(notificationType)
                .notificationBody(body)
                .redirectTarget(redirectTarget)
                .build();
        notification = notificationRepository.save(notification);
        if (Boolean.TRUE.equals(user.getNotificationConsent().getPushConsent())){
            fcmNotificationService.pushAlarmToToken(notification);
        }
    }

    @EventListener(StoreManagerInviteAcceptedNotificationEvent.class)
    public void handle(StoreManagerInviteAcceptedNotificationEvent event) {
        NotificationType notificationType = NotificationType.STORE_MANAGER_INVITE_ACCEPTED;
        RedirectTarget redirectTarget = RedirectTarget.builder()
                .redirectType(RedirectType.STORE_MANAGER_LIST)
                .redirectId(event.getStore().getId())
                .build();

        List<StoreManager> storeManagers = storeManagerRepository.findAllByStoreId(event.getStore().getId());
        for (StoreManager storeManager : storeManagers) {
            String body = notificationType.format(Map.of(
                    "storeName", event.getStore().getName()
            ));
            Notification notification = Notification.builder()
                    .userId(storeManager.getUserId())
                    .notificationType(notificationType)
                    .notificationBody(body)
                    .redirectTarget(redirectTarget)
                    .build();
            notification = notificationRepository.save(notification);
            if (Boolean.TRUE.equals(AuthResolver.resolveUser(storeManager.getUserId()).getNotificationConsent().getPushConsent())) {
                fcmNotificationService.pushAlarmToToken(notification);
            }
        }
    }

    @EventListener(StoreOwnerChangedNotificationEvent.class)
    public void handle(StoreOwnerChangedNotificationEvent event) {
        NotificationType notificationType = NotificationType.STORE_OWNER_CHANGED;
        RedirectTarget redirectTarget = RedirectTarget.builder()
                .redirectType(RedirectType.STORE_MANAGER_LIST)
                .redirectId(event.getStore().getId())
                .build();

        List<StoreManager> storeManagers = storeManagerRepository.findAllByStoreId(event.getStore().getId());
        for (StoreManager storeManager : storeManagers) {
            String body = notificationType.format(Map.of(
                    "storeName", event.getStore().getName()
            ));
            Notification notification = Notification.builder()
                    .userId(storeManager.getUserId())
                    .notificationType(notificationType)
                    .notificationBody(body)
                    .redirectTarget(redirectTarget)
                    .build();
            notification = notificationRepository.save(notification);
            if (Boolean.TRUE.equals(AuthResolver.resolveUser(storeManager.getUserId()).getNotificationConsent().getPushConsent())) {
                fcmNotificationService.pushAlarmToToken(notification);
            }
        }
    }

    @EventListener(StoreOwnershipRequestCreatedNotificationEvent.class)
    public void handle(StoreOwnershipRequestCreatedNotificationEvent event) {
        NotificationType notificationType = NotificationType.STORE_OWNERSHIP_REQUEST_CREATED;
        RedirectTarget redirectTarget = RedirectTarget.builder()
                .redirectType(RedirectType.STORE_DETAIL)
                .redirectId(event.getStore().getId())
                .build();

        List<Auth> adminList = AuthResolver.resolveAdmin();
        for (Auth admin : adminList) {
            String body = notificationType.format(Map.of(
                    "storeName", event.getStore().getName()
            ));
            Notification notification = Notification.builder()
                    .userId(admin.getId())
                    .notificationType(notificationType)
                    .notificationBody(body)
                    .redirectTarget(redirectTarget)
                    .build();
            notification = notificationRepository.save(notification);
            if (Boolean.TRUE.equals(admin.getNotificationConsent().getPushConsent())) {
                fcmNotificationService.pushAlarmToToken(notification);
            }
        }
    }

    @EventListener(StoreSuggestionCreatedNotificationEvent.class)
    public void handle(StoreSuggestionCreatedNotificationEvent event) {
        NotificationType notificationType = NotificationType.STORE_SUGGESTION_CREATED;
        RedirectTarget redirectTarget = RedirectTarget.builder()
                .redirectType(RedirectType.STORE_DETAIL)
                .redirectId(event.getStore().getId())
                .build();

        List<Auth> adminList = AuthResolver.resolveAdmin();
        for (Auth admin : adminList) {
            String body = notificationType.format();
            Notification notification = Notification.builder()
                    .userId(admin.getId())
                    .notificationType(notificationType)
                    .notificationBody(body)
                    .redirectTarget(redirectTarget)
                    .build();
            notification = notificationRepository.save(notification);
            if (Boolean.TRUE.equals(admin.getNotificationConsent().getPushConsent())) {
                fcmNotificationService.pushAlarmToToken(notification);
            }
        }
    }
}
