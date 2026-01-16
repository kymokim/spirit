package com.kymokim.spirit.notification.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.notification.dto.comment.CommentLikedNotificationEvent;
import com.kymokim.spirit.notification.dto.comment.ReplyCommentCreatedNotificationEvent;
import com.kymokim.spirit.notification.dto.comment.RootCommentCreatedNotificationEvent;
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
@MainTransactional
public class CommentNotificationEventHandler {
    private final FCMNotificationService fcmNotificationService;
    private final NotificationRepository notificationRepository;

    @EventListener(RootCommentCreatedNotificationEvent.class)
    public void handle(RootCommentCreatedNotificationEvent event) {
        NotificationType notificationType = NotificationType.ROOT_COMMENT_CREATED;
        RedirectTarget redirectTarget = RedirectTarget.builder()
                .redirectType(RedirectType.COMMENT_DETAIL)
                .redirectId(event.getComment().getId())
                .build();

        Auth writer = event.getWriter();
        String notificationBody = notificationType.format(Map.of(
                "nickName", event.getCommentWriter().getNickname()
        ));
        Notification notification = Notification.builder()
                .userId(writer.getId())
                .notificationType(notificationType)
                .notificationBody(notificationBody)
                .redirectTarget(redirectTarget)
                .imageUrl(event.getCommentWriter().getImgUrl() == null ? null : event.getCommentWriter().getImgUrl())
                .build();
        notification = notificationRepository.save(notification);

        if (Boolean.TRUE.equals(writer.getNotificationConsent().getPushConsent())) {
            fcmNotificationService.pushAlarmToToken(notification);
        }
    }

    @EventListener(ReplyCommentCreatedNotificationEvent.class)
    public void handle(ReplyCommentCreatedNotificationEvent event) {
        NotificationType notificationType = NotificationType.REPLY_COMMENT_CREATED;
        RedirectTarget redirectTarget = RedirectTarget.builder()
                .redirectType(RedirectType.COMMENT_DETAIL)
                .redirectId(event.getComment().getId())
                .build();

        Auth writer = event.getWriter();
        String notificationBody = notificationType.format(Map.of(
                "nickName", event.getReplyWriter().getNickname()
        ));
        Notification notification = Notification.builder()
                .userId(writer.getId())
                .notificationType(notificationType)
                .notificationBody(notificationBody)
                .redirectTarget(redirectTarget)
                .imageUrl(event.getReplyWriter().getImgUrl() == null ? null : event.getReplyWriter().getImgUrl())
                .build();
        notification = notificationRepository.save(notification);

        if (Boolean.TRUE.equals(writer.getNotificationConsent().getPushConsent())) {
            fcmNotificationService.pushAlarmToToken(notification);
        }
    }

    @EventListener(CommentLikedNotificationEvent.class)
    public void handle(CommentLikedNotificationEvent event) {
        NotificationType notificationType = NotificationType.COMMENT_LIKED;
        RedirectTarget redirectTarget = RedirectTarget.builder()
                .redirectType(RedirectType.COMMENT_DETAIL)
                .redirectId(event.getComment().getId())
                .build();

        Auth writer = event.getWriter();
        String notificationBody = notificationType.format(Map.of(
                "nickName", event.getLikedUser().getNickname()
        ));
        Notification notification = Notification.builder()
                .userId(writer.getId())
                .notificationType(notificationType)
                .notificationBody(notificationBody)
                .redirectTarget(redirectTarget)
                .imageUrl(event.getLikedUser().getImgUrl() == null ? null : event.getLikedUser().getImgUrl())
                .build();
        notification = notificationRepository.save(notification);

        if (Boolean.TRUE.equals(writer.getNotificationConsent().getPushConsent())) {
            fcmNotificationService.pushAlarmToToken(notification);
        }
    }
}

