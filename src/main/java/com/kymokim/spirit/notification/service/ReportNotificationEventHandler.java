package com.kymokim.spirit.notification.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.notification.dto.report.ReportReceivedNotificationEvent;
import com.kymokim.spirit.notification.dto.store.*;
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
public class ReportNotificationEventHandler {
    private final FCMNotificationService fcmNotificationService;
    private final NotificationRepository notificationRepository;

    @EventListener(ReportReceivedNotificationEvent.class)
    public void handle(ReportReceivedNotificationEvent event) {
        NotificationType notificationType = NotificationType.REPORT_RECEIVED;
        RedirectTarget redirectTarget = RedirectTarget.builder()
                .redirectType(RedirectType.ADMIN_REPORT_LIST)
                .redirectId(event.getReportId())
                .build();
        String notificationBody = notificationType.format(Map.of(
                "targetDisplayName", event.getTargetDisplayName()
        ));

        List<Auth> adminList = AuthResolver.resolveAdmin();
        for (Auth admin : adminList) {
            Notification notification = Notification.builder()
                    .userId(admin.getId())
                    .notificationType(notificationType)
                    .notificationBody(notificationBody)
                    .redirectTarget(redirectTarget)
                    .build();
            notification = notificationRepository.save(notification);
            if (Boolean.TRUE.equals(admin.getNotificationConsent().getPushConsent())) {
                fcmNotificationService.pushAlarmToToken(notification);
            }
        }
    }
}
