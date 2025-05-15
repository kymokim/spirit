package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.StoreManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreManagerRepository extends JpaRepository<StoreManager, Long> {
    StoreManager findByUserIdAndStoreId(Long userId, Long storeId);
    Page<StoreManager> findByUserIdOrderByApprovedAtDesc(Long userId, Pageable pageable);

}
