package com.kymokim.spirit.auth.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.notification.repository.NotificationRepository;
import com.kymokim.spirit.store.entity.LikedStore;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.entity.StoreManager;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.LikedStoreRepository;
import com.kymokim.spirit.store.repository.OwnershipRequestRepository;
import com.kymokim.spirit.store.repository.StoreManagerRepository;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@MainTransactional
public class MainDataCleaner {

    private final LikedStoreRepository likedStoreRepository;
    private final StoreRepository storeRepository;
    private final NotificationRepository notificationRepository;
    private final StoreManagerRepository storeManagerRepository;
    private final OwnershipRequestRepository ownershipRequestRepository;

    public void cleanUp(Auth user) {
        List<LikedStore> likedStoreList = likedStoreRepository.findAllByUserId(user.getId());
        if (likedStoreList != null) {
            likedStoreList.forEach(likedStore -> {
                Store store = storeRepository.findById(likedStore.getStoreId())
                        .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
                store.decreaseLikeCount();
                likedStoreRepository.delete(likedStore);
            });
        }
        notificationRepository.deleteAllByUserId(user.getId());
        storeManagerRepository.deleteAllByUserId(user.getId());
        List<Store> ownedStores = storeRepository.findByOwnerId(user.getId());
        for (Store store : ownedStores) {
            List<StoreManager> managers = storeManagerRepository.findByStoreIdOrderByApprovedAtAsc(store.getId());
            if (!managers.isEmpty()) {
                store.setOwnerId(managers.getFirst().getUserId());
            } else {
                store.setOwnerId(null);
            }
        }
        ownershipRequestRepository.deleteAllByRequesterId(user.getId());
    }
}
