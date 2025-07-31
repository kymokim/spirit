package com.kymokim.spirit.main.notification.repository;

import com.kymokim.spirit.main.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Notification> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
    void deleteByCreatedAtBefore(LocalDateTime expireDate);
}
