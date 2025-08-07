package com.kymokim.spirit.notification.service;

import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.notification.dto.ResponseNotification;
import com.kymokim.spirit.notification.entity.Notification;
import com.kymokim.spirit.notification.exception.NotificationErrorCode;
import com.kymokim.spirit.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@MainTransactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private Notification resolveNotification(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }

    public void readNotification(Long notificationId) {
        Notification notification = resolveNotification(notificationId);
        if (!Objects.equals(notification.getUserId(), AuthResolver.resolveUserId())) {
            throw new CustomException(NotificationErrorCode.NOTIFICATION_USER_UNMATCHED);
        }
        notification.readNotification();
        notificationRepository.save(notification);
    }

    public void readAllNotification() {
        notificationRepository.findAllByUserId(AuthResolver.resolveUserId())
                .forEach(Notification::readNotification);
    }

    public Page<ResponseNotification.NotificationResponseDto> getReceivedNotification(Pageable pageable) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(AuthResolver.resolveUserId(), pageable)
                .map(ResponseNotification.NotificationResponseDto::toDto);
    }
}
