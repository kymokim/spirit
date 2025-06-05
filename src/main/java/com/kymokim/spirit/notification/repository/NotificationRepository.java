package com.kymokim.spirit.notification.repository;

import com.kymokim.spirit.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByAuthIdOrderByCreatedAtDesc(Long authId, Pageable pageable);
    List<Notification> findAllByAuthId(Long authId);
}
