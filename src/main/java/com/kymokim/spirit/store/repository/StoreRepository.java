package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {
    List<Store> findByIdIn(List<Long> ids);
    List<Store> findByOwnerId(Long ownerId);
}