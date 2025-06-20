package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.entity.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {
    Optional<StoreImage> findByUrl(String url);
    List<StoreImage> findAllByUrlIn(List<String> urlList);
}