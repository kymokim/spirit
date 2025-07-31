package com.kymokim.spirit.main.store.repository;

import com.kymokim.spirit.main.store.dto.RequestStore;
import com.kymokim.spirit.main.store.dto.ResponseStore;

import java.util.List;

public interface LikedStoreRepositoryCustom {
    List<ResponseStore.LikedStoreStatDto> getLikedStoreStats(RequestStore.LikedStoreStatFilter filter);
}
