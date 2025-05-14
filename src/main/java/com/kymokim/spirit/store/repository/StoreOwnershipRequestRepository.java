package com.kymokim.spirit.store.repository;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.store.entity.OwnershipRequest;
import com.kymokim.spirit.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreOwnershipRequestRepository extends JpaRepository <OwnershipRequest, Long> {
    boolean existsByRequesterAndStore(Auth requester, Store store);
    Page<OwnershipRequest> findAllByOrderByRequestedAtAsc(Pageable pageable);
    List<OwnershipRequest> findAllByStore(Store store);


}
