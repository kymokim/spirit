package com.kymokim.spirit.notification.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.notification.dto.post.PostCreatedNotificationEvent;
import com.kymokim.spirit.notification.entity.Notification;
import com.kymokim.spirit.notification.entity.NotificationType;
import com.kymokim.spirit.notification.entity.RedirectTarget;
import com.kymokim.spirit.notification.entity.RedirectType;
import com.kymokim.spirit.notification.repository.NotificationRepository;
import com.kymokim.spirit.store.entity.StoreManager;
import com.kymokim.spirit.store.repository.StoreManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@MainTransactional
public class PostNotificationEventHandler {
    private final FCMNotificationService fcmNotificationService;
    private final NotificationRepository notificationRepository;
    private final StoreManagerRepository storeManagerRepository;

    @EventListener(PostCreatedNotificationEvent.class)
    public void handle(PostCreatedNotificationEvent event) {
        NotificationType notificationType = NotificationType.STORE_TAG_POST_CREATED;
        RedirectTarget redirectTarget = RedirectTarget.builder()
                .redirectType(RedirectType.POST_DETAIL)
                .redirectId(event.getPostId())
                .build();

        List<StoreManager> storeManagers = storeManagerRepository.findAllByStoreId(event.getStore().getId());
        for (StoreManager storeManager : storeManagers) {
            String notificationBody = notificationType.format(Map.of(
                    "storeName", event.getStore().getName()
            ));
            Notification notification = Notification.builder()
                    .userId(storeManager.getUserId())
                    .notificationType(notificationType)
                    .notificationBody(notificationBody)
                    .redirectTarget(redirectTarget)
                    .build();
            notification = notificationRepository.save(notification);

            Auth user = AuthResolver.resolveUser(storeManager.getUserId());
            if (Boolean.TRUE.equals(user.getNotificationConsent().getPushConsent())) {
                fcmNotificationService.pushAlarmToToken(notification);
            }
        }
    }
}

