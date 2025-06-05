package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.StoreManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreManagerRepository extends JpaRepository<StoreManager, Long> {
    StoreManager findByUserIdAndStoreId(Long userId, Long storeId);
    Page<StoreManager> findByUserIdOrderByApprovedAtDesc(Long userId, Pageable pageable);
    List<StoreManager> findByStoreIdOrderByApprovedAtAsc(Long storeId);
    void deleteAllByUserId(Long userId);
}
