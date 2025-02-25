package com.kymokim.spirit.archive.service;

import com.kymokim.spirit.archive.entity.ArchiveType;
import com.kymokim.spirit.archive.entity.UserArchive;
import com.kymokim.spirit.archive.repository.UserArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final UserArchiveRepository userArchiveRepository;

    @Transactional
    public void archiveUser(Long originalId, String encryptedCi, ArchiveType type){
        UserArchive userArchive = UserArchive.builder()
                .originalId(originalId)
                .ci(encryptedCi)
                .type(type)
                .expirationDate(LocalDateTime.now().plusMonths(6))
                .build();
        userArchiveRepository.save(userArchive);
    }
}
