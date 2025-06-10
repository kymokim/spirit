package com.kymokim.spirit.auth.service;

import com.kymokim.spirit.archive.entity.ArchiveType;
import com.kymokim.spirit.archive.service.ArchiveService;
import com.kymokim.spirit.auth.entity.*;
import com.kymokim.spirit.auth.dto.RequestAuth;
import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.repository.AuthRepository;
import com.kymokim.spirit.auth.repository.NotificationConsentRepository;
import com.kymokim.spirit.auth.repository.SocialInfoRepository;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.security.JwtTokenProvider;
import com.kymokim.spirit.common.service.AESUtil;
import com.kymokim.spirit.common.service.S3Service;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final S3Service s3Service;
    private final ArchiveService archiveService;
    private final LikedStoreRepository likedStoreRepository;
    private final StoreRepository storeRepository;
    private final SocialInfoRepository socialInfoRepository;
    private final NotificationConsentRepository notificationConsentRepository;
    private final NotificationRepository notificationRepository;
    private final StoreManagerRepository storeManagerRepository;
    private final OwnershipRequestRepository ownershipRequestRepository;

    private Auth resolveUser() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        Auth user = authRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        return user;
    }

    @Transactional
    public void mergeUser(RequestAuth.MergeUserDto mergeUserDto) {
        Auth originalUser = authRepository.findBySocial(mergeUserDto.getOriginalSocialInfoDto().getSocialType(), mergeUserDto.getOriginalSocialInfoDto().getSocialId());
        if (originalUser == null) {
            throw new CustomException(AuthErrorCode.USER_NOT_FOUND);
        }

        Auth newUser = authRepository.findBySocial(mergeUserDto.getNewSocialInfoDto().getSocialType(), mergeUserDto.getNewSocialInfoDto().getSocialId());
        if (newUser != null) {
            withdraw(newUser);
        }
        SocialInfo newSocialInfo = mergeUserDto.getNewSocialInfoDto().toEntity();
        originalUser.addSocialInfo(newSocialInfo);
        socialInfoRepository.save(newSocialInfo);
        authRepository.save(originalUser);
    }

    @Transactional
    public void withdrawUser() {
        Auth user = resolveUser();
        withdraw(user);
    }

    @Transactional
    private void withdraw(Auth user){
        if (!Objects.equals(user.getImgUrl(), null) && !user.getImgUrl().isEmpty()) {
            s3Service.deleteFile(user.getImgUrl());
            user.setImgUrl(null);
        }
        List<LikedStore> likedStoreList = likedStoreRepository.findAllByUserId(user.getId());
        if (likedStoreList != null) {
            likedStoreList.forEach(likedStore -> {
                Store store = storeRepository.findById(likedStore.getStoreId())
                        .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
                store.decreaseLikeCount();
                likedStoreRepository.delete(likedStore);
            });
        }
        for (SocialInfo socialInfo : new ArrayList<>(user.getSocialInfoList())) {
            user.getSocialInfoList().remove(socialInfo);
            socialInfo.setAuth(null);
            socialInfoRepository.delete(socialInfo);
        }
        if (!Objects.equals(user.getPersonalInfo().getCi(), null)) {
            archiveService.archiveUser(user.getId(), user.getPersonalInfo().getCi(), ArchiveType.WITHDREW);
        }
        if (user.getNotificationConsent() != null) {
            NotificationConsent notificationConsent = user.getNotificationConsent();
            user.initNotificationConsent(null);
            notificationConsentRepository.delete(notificationConsent);
        }
        notificationRepository.deleteAllByAuthId(user.getId());

        List<Store> ownedStores = storeRepository.findByOwnerId(user.getId());
        for (Store store : ownedStores) {
            List<StoreManager> managers = storeManagerRepository.findByStoreIdOrderByApprovedAtAsc(store.getId());
            if (!managers.isEmpty()) {
                store.setOwnerId(managers.getFirst().getUserId());
            } else {
                store.setOwnerId(null);
            }
        }

        storeManagerRepository.deleteAllByUserId(user.getId());
        ownershipRequestRepository.deleteAllByRequesterId(user.getId());

        user.withdraw();
        authRepository.save(user);
    }
}