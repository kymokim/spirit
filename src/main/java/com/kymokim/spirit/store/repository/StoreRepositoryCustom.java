package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.dto.StoreSearchCriteria;
import com.kymokim.spirit.store.entity.Store;

import java.util.List;

public interface StoreRepositoryCustom {

    List<Store> findStoresByCategory(StoreSearchCriteria criteria, String category);

    List<Store> findStoresByDistance(StoreSearchCriteria criteria);
}
