package com.kymokim.spirit.archive.repository;

import com.kymokim.spirit.archive.entity.UserArchive;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UserArchiveRepository extends JpaRepository<UserArchive, Long> {

    // 만료일(expirationDate)이 현재시간 이전인 데이터를 삭제
    @Transactional
    void deleteByExpirationDateBefore(LocalDateTime currentDateTime);

}
