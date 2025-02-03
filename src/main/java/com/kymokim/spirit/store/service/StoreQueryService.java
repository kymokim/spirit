package com.kymokim.spirit.store.service;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.review.repository.ReviewRepository;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.entity.LikedStore;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.LikedStoreRepository;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreQueryService {

    private final StoreRepository storeRepository;
    private final LikedStoreRepository likedStoreRepository;
    private final ReviewRepository reviewRepository;

    private Long resolveUserId(){
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private Store resolveStore(Long storeId){
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private double calculateRate(Store store){
        double rateAvg = store.getTotalRate() / store.getReviewCount();
        return Math.round(rateAvg * 100.0) / 100.0;
    }

    @Deprecated
    @Transactional
    public List<ResponseStore.GetAllStoreDto> getAllStore(){
        List<Store> storeList = storeRepository.findAll();
        List<ResponseStore.GetAllStoreDto> getAllStoreDtoList = new ArrayList<>();
        storeList.forEach(store -> getAllStoreDtoList.add(ResponseStore.GetAllStoreDto.toDto(store)));
        return getAllStoreDtoList;
    }

    @Transactional
    public ResponseStore.GetStoreDto getStore(Long storeId) {
        Store store = resolveStore(storeId);
        boolean isStoreLiked = false;
        LikedStore likedStore = likedStoreRepository.findByUserIdAndStoreId(resolveUserId(), storeId);
        if (likedStore != null) {
            isStoreLiked = true;
        }
        return ResponseStore.GetStoreDto.toDto(store, calculateRate(store), isStoreLiked);
    }

    @Transactional
    public Page<ResponseStore.SearchStoreDto> searchStore(LocationCriteria criteria, String searchKeyword, Pageable pageable){
        Page<Store> storePage = storeRepository.findByNameAndMenu(criteria, searchKeyword, pageable);
        return storePage.map(store -> ResponseStore.SearchStoreDto.toDto(store, calculateRate(store)));
    }

    @Transactional
    public Page<ResponseStore.GetByDistanceDto> getByDistance(LocationCriteria criteria, Pageable pageable){
        Page<Store> storePage = storeRepository.findByDistance(criteria, pageable);
        return storePage.map(store -> ResponseStore.GetByDistanceDto.toDto(store, calculateRate(store)));
    }

    @Transactional
    public Page<ResponseStore.GetByCategoryDto> getByCategory(LocationCriteria criteria, String category, Pageable pageable) {
        Page<Store> storePage = storeRepository.findByCategory(criteria, category, pageable);
        return storePage.map(store -> ResponseStore.GetByCategoryDto.toDto(store, calculateRate(store)));
    }

    @Transactional
    public Page<ResponseStore.GetByBusinessHoursDto> getByBusinessHours(LocationCriteria criteria, Pageable pageable) {
        Page<Store> storePage = storeRepository.findByBusinessHours(criteria, pageable);
        return storePage.map(ResponseStore.GetByBusinessHoursDto::toDto);
    }

    @Transactional
    public List<ResponseStore.GetByRadiusDto> getByRadius(LocationCriteria criteria){
        List<Store> storeList = storeRepository.findByRadius(criteria);
        List<ResponseStore.GetByRadiusDto> dtoList = new ArrayList<>();
        storeList.forEach(store -> dtoList.add(ResponseStore.GetByRadiusDto.toDto(store, calculateRate(store))));
        return dtoList;
    }

    @Transactional
    public Page<ResponseStore.GetLikedStoreDto> getLikedStore(Pageable pageable){
        Long userId = resolveUserId();
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
    }

    @Transactional
    public Page<ResponseStore.GetRecentStoreDto> getRecentStore(Pageable pageable) {
        Long userId = resolveUserId();
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
    }

    @Transactional
    public Page<ResponseStore.SearchStoreDto> conditionSearchStore(LocationCriteria criteria, RequestStore.ConditionSearchDto conditionSearchDto, Pageable pageable){
        Page<Store> storePage = storeRepository.findByMultipleCondition(criteria, conditionSearchDto.getCategory(), conditionSearchDto.getIsGroupAvailable(), conditionSearchDto.getConditionTime(), pageable);
        return storePage.map(store -> ResponseStore.SearchStoreDto.toDto(store, calculateRate(store)));
    }
}
