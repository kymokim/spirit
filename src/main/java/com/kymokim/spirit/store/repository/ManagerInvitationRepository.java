package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.ManagerInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ManagerInvitationRepository extends JpaRepository<ManagerInvitation, String> {
    void deleteByExpiresAtBefore(LocalDateTime now);
}
