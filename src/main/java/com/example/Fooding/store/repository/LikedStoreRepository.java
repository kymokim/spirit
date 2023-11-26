package com.example.Fooding.store.repository;

import com.example.Fooding.store.entity.LikedStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedStoreRepository extends JpaRepository<LikedStore, Long> {
    LikedStore findByUserIdAndStoreId(Long userId, Long storeId);
}
