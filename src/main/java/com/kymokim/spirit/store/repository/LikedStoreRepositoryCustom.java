package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;

import java.util.List;

public interface LikedStoreRepositoryCustom {
    List<ResponseStore.LikedStoreStatDto> getLikedStoreStats(RequestStore.LikedStoreStatFilter filter);
}
