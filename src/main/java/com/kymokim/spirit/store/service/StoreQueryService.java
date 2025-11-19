package com.kymokim.spirit.store.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Role;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.AESUtil;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.drink.entity.Drink;
import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.log.repository.StoreViewLogRepository;
import com.kymokim.spirit.log.service.LogService;
import com.kymokim.spirit.report.entity.ReportReason;
import com.kymokim.spirit.report.entity.ReportStatus;
import com.kymokim.spirit.report.entity.ReportTarget;
import com.kymokim.spirit.report.repository.ReportRepository;
import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.review.repository.ReviewRepository;
import com.kymokim.spirit.store.dto.QueryStore;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.entity.*;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.*;
import com.kymokim.spirit.store.repository.dto.StoreMarkerProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@MainTransactional(readOnly = true)
public class StoreQueryService {

    private final StoreRepository storeRepository;
    private final LikedStoreRepository likedStoreRepository;
    private final ReviewRepository reviewRepository;
    private final OwnershipRequestRepository ownershipRequestRepository;
    private final StoreManagerRepository storeManagerRepository;
    private final AESUtil aesUtil;
    private final ReportRepository reportRepository;
    private final StoreSuggestionRepository storeSuggestionRepository;
    private final ManagerInvitationRepository managerInvitationRepository;
    private final LogService logService;
    private final StoreViewLogRepository storeViewLogRepository;

    private Store resolveStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private OwnershipRequest resolveOwnership(Long ownershipId) {
        return ownershipRequestRepository.findById(ownershipId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.OWNERSHIP_NOT_FOUND));
    }

    public void validateStoreAccess(Long storeId) {
        Auth user = AuthResolver.resolveUser();
        boolean isAdmin = user.getRoles().contains(Role.ADMIN);
        boolean isManager = storeManagerRepository.existsByUserIdAndStoreId(user.getId(), storeId);
        if (!(isAdmin || isManager)) {
            throw new CustomException(StoreErrorCode.STORE_UNAUTHORIZED_ACCESS);
        }
    }

    private double calculateRate(Store store) {
        double rateAvg = store.getTotalRate() / store.getReviewCount();
        return Math.round(rateAvg * 100.0) / 100.0;
    }

    public ResponseStore.GetStoreDto getStore(Long storeId) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Store store = resolveStore(storeId);
            Auth user = AuthResolver.resolveUser();
            boolean isStoreLiked = false;
            LikedStore likedStore = likedStoreRepository.findByUserIdAndStoreId(user.getId(), storeId);
            if (likedStore != null) {
                isStoreLiked = true;
            }
            Boolean isOwner;
            Boolean isUpdatable = false;
            Long ownerId = store.getOwnerId();
            StoreManager storeManager = storeManagerRepository.findByUserIdAndStoreId(user.getId(), storeId);

            if (user.getRoles().contains(Role.ADMIN)) {
                isUpdatable = true;
            }
            if (ownerId == null) {
                isOwner = null;
            } else if (storeManager != null) {
                isOwner = true;
                isUpdatable = true;
            } else {
                isOwner = false;
            }
            //todo event raise 방식으로 비동기 처리
            logService.createStoreViewLog(storeId);
            return ResponseStore.GetStoreDto.toDto(store, isOwner, isUpdatable, calculateRate(store), isStoreLiked);
        }, 3);
    }

    public ResponseStore.GetStorePreviewDto getStorePreview(Long storeId) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Store store = resolveStore(storeId);
            Auth user = AuthResolver.resolveUser();
            boolean isStoreLiked = false;
            LikedStore likedStore = likedStoreRepository.findByUserIdAndStoreId(user.getId(), storeId);
            if (likedStore != null) {
                isStoreLiked = true;
            }
            return ResponseStore.GetStorePreviewDto.toDto(store, calculateRate(store), isStoreLiked);
        }, 3);
    }

    public Page<ResponseStore.SearchStoreDto> searchStore(LocationCriteria criteria, String searchKeyword, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Store> storePage = storeRepository.findByNameAndMenu(criteria, searchKeyword, pageable);
            return storePage.map(store -> ResponseStore.SearchStoreDto.toDto(store, calculateRate(store)));
        }, 3);
    }

    public Page<ResponseStore.SearchAllStoreDto> searchAllStore(String searchKeyword, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Store> storePage = storeRepository.findByName(searchKeyword, pageable);
            return storePage.map(ResponseStore.SearchAllStoreDto::toDto);
        }, 3);
    }

    public Page<ResponseStore.GetByDistanceDto> getByDistance(LocationCriteria criteria, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Store> storePage = storeRepository.findByDistance(criteria, pageable);
            return storePage.map(store -> ResponseStore.GetByDistanceDto.toDto(store, calculateRate(store)));
        }, 3);
    }

    public Page<ResponseStore.GetByCategoryDto> getByCategory(LocationCriteria criteria, String category, DrinkType drinkType, Sort.Direction priceOrder, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Store> storePage = storeRepository.findByCategory(criteria, category, drinkType, priceOrder, pageable);
            return storePage.map(store -> ResponseStore.GetByCategoryDto.toDto(store, calculateRate(store), drinkType));
        }, 3);
    }

    public Page<ResponseStore.GetByBusinessHoursDto> getByBusinessHours(LocationCriteria criteria, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Store> storePage = storeRepository.findByBusinessHours(criteria, pageable);
            return storePage.map(store -> ResponseStore.GetByBusinessHoursDto.toDto(store, calculateRate(store)));
        }, 3);
    }

    public Page<ResponseStore.GetLikedStoreDto> getLikedStore(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Long userId = AuthResolver.resolveUserId();
            Page<LikedStore> likedStorePage = likedStoreRepository.findAllByUserIdOrderByIdDesc(userId, pageable);

            List<Long> storeIdList = likedStorePage.stream()
                    .map(LikedStore::getStoreId)
                    .collect(Collectors.toList());

            Map<Long, Store> storeMap = storeRepository.findByIdIn(storeIdList).stream()
                    .collect(Collectors.toMap(Store::getId, store -> store));

            return likedStorePage.map(likedStore -> {
                Store store = storeMap.get(likedStore.getStoreId());
                return ResponseStore.GetLikedStoreDto.toDto(store, calculateRate(store));
            });
        }, 3);
    }

    public Page<ResponseStore.GetRecentStoreDto> getRecentStore(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Long userId = AuthResolver.resolveUserId();
            Page<Review> reviewPage = reviewRepository.findAllByWriterIdOrderByHistoryInfo_CreatedAtDesc(userId, pageable);

            List<Long> storeIdList = reviewPage.stream()
                    .map(review -> review.getStore().getId())
                    .collect(Collectors.toList());

            Map<Long, Store> storeMap = storeRepository.findByIdIn(storeIdList).stream()
                    .collect(Collectors.toMap(Store::getId, store -> store));

            return reviewPage.map(review -> {
                Store store = storeMap.get(review.getStore().getId());
                return ResponseStore.GetRecentStoreDto.toDto(store, calculateRate(store));
            });
        }, 3);
    }

    public Page<ResponseStore.SearchStoreDto> conditionSearchStore(LocationCriteria criteria, RequestStore.ConditionSearchDto conditionSearchDto, Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Store> storePage = storeRepository.findByMultipleCondition(
                    criteria,
                    conditionSearchDto.getCategory(),
                    conditionSearchDto.getSearchKeyword(),
                    conditionSearchDto.toFacilitiesCondition(),
                    conditionSearchDto.getConditionTime(),
                    conditionSearchDto.getDrinkType(),
                    conditionSearchDto.getMoods(),
                    conditionSearchDto.getPriceOrder(),
                    pageable
            );
            return storePage.map(store -> ResponseStore.SearchStoreDto.toDto(store, calculateRate(store), conditionSearchDto.getDrinkType()));
        }, 3);
    }

    public List<ResponseStore.MapMarkerDto> conditionSearchStoreMarkers(LocationCriteria criteria, RequestStore.ConditionSearchDto conditionSearchDto) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            List<StoreMarkerProjection> markerProjections = storeRepository.findMarkersByMultipleCondition(
                    criteria,
                    conditionSearchDto.getCategory(),
                    conditionSearchDto.getSearchKeyword(),
                    conditionSearchDto.toFacilitiesCondition(),
                    conditionSearchDto.getConditionTime(),
                    conditionSearchDto.getDrinkType(),
                    conditionSearchDto.getMoods()
            );
            return markerProjections.stream()
                    .map(ResponseStore.MapMarkerDto::toDto)
                    .collect(Collectors.toList());
        }, 3);
    }

    public ResponseStore.GetMainBannerDto getMainBanner(LocationCriteria criteria) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            QueryStore.CategoryStoreListGroup categoryStoreListGroup = storeRepository.findByRadiusAndCategory(criteria);
            return ResponseStore.GetMainBannerDto.toDto(categoryStoreListGroup.category(), categoryStoreListGroup.storeList());
        }, 3);
    }

    public Page<ResponseStore.StoreSuggestionListDto> getStoreSuggestionList(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            List<Long> storeIdsWithOwnership = ownershipRequestRepository.findAllStoreIdsWithOwnershipRequest();

            Page<StoreSuggestion> storeSuggestionPage;
            if (storeIdsWithOwnership.isEmpty()) {
                storeSuggestionPage = storeSuggestionRepository.findAllByOrderBySuggestedAtAsc(pageable);
            } else {
                storeSuggestionPage = storeSuggestionRepository.findByStoreIdNotInOrderBySuggestedAtAsc(storeIdsWithOwnership, pageable);
            }

            return storeSuggestionPage.map(ResponseStore.StoreSuggestionListDto::toDto);
        }, 3);
    }

    public Page<ResponseStore.OwnershipListDto> getOwnershipList(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<OwnershipRequest> ownershipRequestPage = ownershipRequestRepository.findByStoreIsDeletedFalseOrderByRequestedAtAsc(pageable);
            return ownershipRequestPage.map(ResponseStore.OwnershipListDto::toDto);
        }, 3);
    }

    public Page<ResponseStore.GetOwnershipListWithStoreSuggestionDto> getOwnershipListWithStoreSuggestion(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<OwnershipRequest> ownershipRequestPage = ownershipRequestRepository.findByStoreIsDeletedTrueOrderByRequestedAtAsc(pageable);
            return ownershipRequestPage.map(ownershipRequest -> {

                ResponseStore.OwnershipListDto ownershipListDto = ResponseStore.OwnershipListDto.toDto(ownershipRequest);

                StoreSuggestion storeSuggestion = storeSuggestionRepository.findByStoreId(ownershipRequest.getStore().getId());
                ResponseStore.StoreSuggestionListDto storeSuggestionListDto = null;

                if (storeSuggestion != null) {
                    storeSuggestionListDto = ResponseStore.StoreSuggestionListDto.toDto(storeSuggestion);
                }

                return ResponseStore.GetOwnershipListWithStoreSuggestionDto.builder()
                        .ownershipListDto(ownershipListDto)
                        .storeSuggestionListDto(storeSuggestionListDto)
                        .build();
            });
        }, 3);
    }

    public ResponseStore.OwnershipDto getOwnership(Long ownershipId) {
        OwnershipRequest receviedOwnershipRequest = resolveOwnership(ownershipId);

        List<OwnershipRequest> ownershipRequests = ownershipRequestRepository.findAllByStore(receviedOwnershipRequest.getStore());
        List<ResponseStore.OwnershipListDto> ownershipList = new ArrayList<>();
        ownershipRequests.forEach(ownershipRequest -> ownershipList.add(ResponseStore.OwnershipListDto.toDto(ownershipRequest)));

        return ResponseStore.OwnershipDto.toDto(receviedOwnershipRequest, ownershipList, aesUtil);

    }

    public Page<ResponseStore.ManagedStoreListDto> getManagedStoreList(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            List<ReportReason> normalReasons = List.of(
                    ReportReason.INCORRECT_STORE_INFO,
                    ReportReason.INCORRECT_MENU_INFO,
                    ReportReason.INCORRECT_DRINK_INFO,
                    ReportReason.NON_EXISTENT_STORE,
                    ReportReason.DUPLICATE_STORE,
                    ReportReason.ETC
            );
            List<ReportReason> priorityReasons = List.of(
                    ReportReason.INAPPROPRIATE_LANGUAGE,
                    ReportReason.INAPPROPRIATE_PHOTO,
                    ReportReason.VIOLATION_OF_GUIDELINES
            );
            Page<StoreManager> storeManagerPage = storeManagerRepository.findByUserIdOrderByApprovedAtDesc(AuthResolver.resolveUserId(), pageable);

            return storeManagerPage.map(managedStore -> {
                Store store = resolveStore(managedStore.getStoreId());
                Long normalReportCount = reportRepository.countByReportTargetAndTargetIdAndReportStatusAndReportReasonIn(
                        ReportTarget.STORE, store.getId(), ReportStatus.PENDING, normalReasons
                );
                Long priorityReportCount = reportRepository.countByReportTargetAndTargetIdAndReportStatusAndReportReasonIn(
                        ReportTarget.STORE, store.getId(), ReportStatus.PENDING, priorityReasons
                );

                return ResponseStore.ManagedStoreListDto.toDto(managedStore, store, calculateRate(store), normalReportCount, priorityReportCount);
            });
        }, 3);
    }


    public List<ResponseStore.StoreManagerListDto> getStoreManagers(Long storeId) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            validateStoreAccess(storeId);
            Store store = resolveStore(storeId);
            List<StoreManager> managers = storeManagerRepository.findByStoreIdOrderByApprovedAtAsc(storeId);
            return managers.stream()
                    .map(storeManager -> {
                        Auth managerUser = AuthResolver.resolveUser(storeManager.getUserId());
                        return ResponseStore.StoreManagerListDto.toDto(
                                storeManager, store.getOwnerId(), managerUser.getNickname(), managerUser.getImgUrl()
                        );
                    })
                    .toList();
        }, 3);
    }

    public List<ResponseStore.LikedStoreStatDto> getLikedStoreStats(RequestStore.LikedStoreStatFilter filter) {
        return TransactionRetryUtil.executeWithRetry(() -> likedStoreRepository.getLikedStoreStats(filter), 3);
    }

    public Page<ResponseStore.GetViewedStoreDto> getViewedStore(Pageable pageable) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Long userId = AuthResolver.resolveUserId();

            Page<Long> idPage = storeViewLogRepository.findViewedStoreIds(userId, pageable);
            List<Long> idList = idPage.getContent();
            if (idList.isEmpty()) {
                return new PageImpl<>(List.of(), pageable, idPage.getTotalElements());
            }

            Map<Long, Store> storeMap = storeRepository.findByIdIn(idList).stream()
                    .collect(Collectors.toMap(Store::getId, store -> store));

            List<ResponseStore.GetViewedStoreDto> content = new ArrayList<>(idList.size());
            for (Long id : idList) {
                Store store = storeMap.get(id);
                if (store != null) {
                    content.add(ResponseStore.GetViewedStoreDto.toDto(store));
                }
            }

            return new PageImpl<>(content, pageable, idPage.getTotalElements());
        }, 3);
    }

    public Page<ResponseStore.GetPopularStoreDto> getPopularStore(LocationCriteria criteria, DrinkType drinkType, Sort.Direction priceOrder, Pageable pageable) {
        Page<Store> storePage = storeRepository.findPopularStore(criteria, drinkType, priceOrder, pageable);
        return storePage.map(store -> ResponseStore.GetPopularStoreDto.toDto(store, calculateRate(store), drinkType));
    }

    public ResponseStore.ManagerInvitationPreviewDto getManagerInvitationPreview(String managerInvitationId) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            ManagerInvitation managerInvitation = managerInvitationRepository.findById(managerInvitationId)
                    .orElseThrow(() -> new CustomException(StoreErrorCode.INVALID_MANAGER_INVITATION_CODE));
            if (managerInvitation.getExpiresAt() != null && managerInvitation.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new CustomException(StoreErrorCode.INVALID_MANAGER_INVITATION_CODE);
            }
            return ResponseStore.ManagerInvitationPreviewDto.toDto(managerInvitation);
        }, 3);
    }
}
