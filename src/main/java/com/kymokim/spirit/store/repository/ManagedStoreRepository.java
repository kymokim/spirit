package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.store.entity.ManagedStore;
import com.kymokim.spirit.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagedStoreRepository extends JpaRepository<ManagedStore, Long> {
    ManagedStore findByUserIdAndStoreId(Long userId, Long storeId);
    Page<ManagedStore> findByUserIdOrderByApprovedAtDesc(Long userId, Pageable pageable);

}
