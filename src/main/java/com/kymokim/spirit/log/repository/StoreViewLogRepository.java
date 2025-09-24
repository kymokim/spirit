package com.kymokim.spirit.log.repository;

import com.kymokim.spirit.log.entity.StoreViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface StoreViewLogRepository extends JpaRepository<StoreViewLog, Long>, StoreViewLogRepositoryCustom {
    StoreViewLog getByUserIdAndStoreIdAndViewDate(Long userId, Long storeId, LocalDate viewDate);
}
