package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.entity.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {
    Optional<StoreImage> findByUrl(String url);
    List<StoreImage> findAllByUrlIn(List<String> urlList);
    @Query("SELECT MAX(si.sortOrder) FROM StoreImage si WHERE si.store.id = :storeId")
    Optional<Integer> findMaxSortOrderByStoreId(@Param("storeId") Long storeId);
}