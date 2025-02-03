package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;



public interface StoreRepositoryCustom {

    Page<Store> findByNameAndMenu(LocationCriteria criteria, String searchKeyword, Pageable pageable);

    Page<Store> findByDistance(LocationCriteria criteria, Pageable pageable);

    Page<Store> findByCategory(LocationCriteria criteria, String category, Pageable pageable);

    Page<Store> findByBusinessHours(LocationCriteria criteria, Pageable pageable);

    List<Store> findByRadius(LocationCriteria criteria);

    Page<Store> findByMultipleCondition(LocationCriteria criteria, String category, Boolean isGroupAvailable, LocalDateTime conditionTime, Pageable pageable);
}
