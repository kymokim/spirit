package com.kymokim.spirit.store.service;

import com.kymokim.spirit.store.repository.ManagerInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExpiredManagerInvitationCleaner {

    private final ManagerInvitationRepository managerInvitationRepository;

    // 매일 0시 만료 초대 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void clean() {
        managerInvitationRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}

