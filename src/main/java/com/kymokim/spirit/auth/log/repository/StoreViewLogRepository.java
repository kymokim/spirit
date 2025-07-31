package com.kymokim.spirit.auth.log.repository;

import com.kymokim.spirit.auth.log.entity.StoreViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface StoreViewLogRepository extends JpaRepository<StoreViewLog, Long>, StoreViewLogRepositoryCustom {
    boolean existsByUserIdAndStoreIdAndViewDate(Long userId, Long storeId, LocalDate viewDate);
}
