package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.Store;

import java.util.List;

public class QueryStore {
    public record CategoryStoreListGroup(Category category, List<Store> storeList) {}
}
