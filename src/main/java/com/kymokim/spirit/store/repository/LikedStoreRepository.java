package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.LikedStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikedStoreRepository extends JpaRepository<LikedStore, Long>, LikedStoreRepositoryCustom {
    LikedStore findByUserIdAndStoreId(Long userId, Long storeId);
    Page<LikedStore> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);
    List<LikedStore> findAllByStoreId(Long storeId);
    List<LikedStore> findAllByUserId(Long userId);
}
