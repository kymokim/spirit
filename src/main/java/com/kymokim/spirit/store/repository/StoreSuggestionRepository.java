package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.StoreSuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreSuggestionRepository extends JpaRepository<StoreSuggestion, Long> {
    Page<StoreSuggestion> findByStoreIdNotInOrderBySuggestedAtAsc(List<Long> excludedStoreIds, Pageable pageable);
    StoreSuggestion findByStoreId(Long storeId);
}
