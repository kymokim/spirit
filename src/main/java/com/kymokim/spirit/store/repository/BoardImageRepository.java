package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
    Optional<BoardImage> findByUrl(String url);
    List<BoardImage> findAllByUrlIn(List<String> urlList);
    @Query("SELECT MAX(si.sortOrder) FROM BoardImage si WHERE si.store.id = :storeId")
    Optional<Integer> findMaxSortOrderByStoreId(@Param("storeId") Long storeId);
}