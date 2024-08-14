package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.dto.StoreSearchCriteria;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.Store;

import java.util.List;
import java.util.Set;

public interface StoreRepositoryCustom {

    List<Store> findStoresByCategory(StoreSearchCriteria criteria, String category);

    List<Store> findStoresByDistance(StoreSearchCriteria criteria);
}
