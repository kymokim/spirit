package com.kymokim.spirit.main.store.dto;

import com.kymokim.spirit.main.store.entity.Category;
import com.kymokim.spirit.main.store.entity.Store;

import java.util.List;

public class QueryStore {
    public record CategoryStoreListGroup(Category category, List<Store> storeList) {}
}
