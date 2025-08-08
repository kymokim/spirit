package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.store.entity.OwnershipRequest;
import com.kymokim.spirit.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OwnershipRequestRepository extends JpaRepository <OwnershipRequest, Long> {
    boolean existsByRequesterIdAndStore(Long requesterId, Store store);
    Page<OwnershipRequest> findByStoreIsDeletedFalseOrderByRequestedAtAsc(Pageable pageable);
    List<OwnershipRequest> findAllByStore(Store store);
    void deleteAllByRequesterId(Long requesterId);
    Page<OwnershipRequest> findByStoreIsDeletedTrueOrderByRequestedAtAsc(Pageable pageable);
    @Query("SELECT DISTINCT o.store.id FROM OwnershipRequest o")
    List<Long> findAllStoreIdsWithOwnershipRequest();
}
