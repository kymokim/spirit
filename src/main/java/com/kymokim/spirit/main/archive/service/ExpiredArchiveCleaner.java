package com.kymokim.spirit.main.archive.service;

import com.kymokim.spirit.main.archive.repository.UserArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExpiredArchiveCleaner {

    private final UserArchiveRepository userArchiveRepository;

    // 매일 06시에 만료 데이터 삭제
    @Transactional
    @Scheduled(cron = "0 0 6 * * *")
    public void cleanExpiredArchive(){
        userArchiveRepository.deleteByExpirationDateBefore(LocalDateTime.now());
    }
}
