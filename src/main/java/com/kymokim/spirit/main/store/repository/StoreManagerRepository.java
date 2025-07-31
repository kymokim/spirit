package com.kymokim.spirit.main.store.repository;

import com.kymokim.spirit.main.store.entity.StoreManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StoreManagerRepository extends JpaRepository<StoreManager, Long> {
    StoreManager findByUserIdAndStoreId(Long userId, Long storeId);
    Page<StoreManager> findByUserIdOrderByApprovedAtDesc(Long userId, Pageable pageable);
    List<StoreManager> findByStoreIdOrderByApprovedAtAsc(Long storeId);
    void deleteAllByUserId(Long userId);
    List<StoreManager> findAllByStoreId(Long storeId);
    boolean existsByUserIdAndStoreId(Long userId, Long storeId);
    Long countByApprovedAtBetween(LocalDateTime start, LocalDateTime end);
    Long countByApprovedAtIsNotNull();
}
