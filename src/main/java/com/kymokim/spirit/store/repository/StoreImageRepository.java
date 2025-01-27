package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.entity.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {
    Optional<StoreImage> findByUrlAndStoreId(String url, Long storeId);
}
