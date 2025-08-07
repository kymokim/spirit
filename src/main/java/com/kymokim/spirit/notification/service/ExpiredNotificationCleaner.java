package com.kymokim.spirit.notification.service;

import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@MainTransactional
public class ExpiredNotificationCleaner {

    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredNotifications() {
        LocalDateTime expireDate = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteByCreatedAtBefore(expireDate);
        log.info("[deleteExpiredNotifications] {} 기준 이전 알림 자동 삭제 완료", expireDate);
    }
}
