package com.kymokim.spirit.store.service;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.review.repository.ReviewRepository;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.dto.StoreSearchCriteria;
import com.kymokim.spirit.store.entity.LikedStore;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.LikedStoreRepository;
import com.kymokim.spirit.store.repository.StoreImageRepository;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
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

    private boolean isStoreOpen(Store store) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek today = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        // 휴무일에 포함되어 있는지 확인
        if (store.getClosedDays().contains(today)) {
            return false;
        }

        // 영업 시간 확인
        LocalTime openHour = store.getBusinessHours().getOpenTime();
        LocalTime closeHour = store.getBusinessHours().getCloseTime();

        // 영업 시간 내인지 확인
        if (openHour.isBefore(closeHour)) { // 정상적인 하루 내 영업 시간
            if (currentTime.isAfter(openHour) && currentTime.isBefore(closeHour)) {
                return true;
            }
        } else { // 자정을 넘어가는 영업 시간
            if (currentTime.isAfter(openHour) || currentTime.isBefore(closeHour)) {
                return true;
            }
        }
        return false;
    }

    private double calculateRate(Store store){
        double rateAvg = store.getTotalRate() / store.getReviewCount();
        return Math.round(rateAvg * 100.0) / 100.0;
    }

    public ResponseStore.GetStoreDto getStore(Long storeId) {
        Store store = resolveStore(storeId);
        double rateAvg = calculateRate(store);
        boolean isStoreLiked = false;
        LikedStore likedStore = likedStoreRepository.findByUserIdAndStoreId(resolveUserId(), storeId);
        if (likedStore != null) {
            isStoreLiked = true;
        }
        return ResponseStore.GetStoreDto.toDto(store, rateAvg, isStoreLiked, isStoreOpen(store));
    }

    public List<ResponseStore.GetAllStoreDto> getAllStore(){
        List<Store> storeList = storeRepository.findAll();
        List<ResponseStore.GetAllStoreDto> getAllStoreDtoList = new ArrayList<>();
        storeList.forEach(store -> getAllStoreDtoList.add(ResponseStore.GetAllStoreDto.toDto(store)));
        return getAllStoreDtoList;
    }

//    public List<ResponseStore.GetLikedStoreDto> getLikedStore(){
//        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
//        List<LikedStore> entityList = likedStoreRepository.findAllByUserId(userId);
//        List<ResponseStore.GetLikedStoreDto> dtoList = new ArrayList<>();
//        entityList.stream().forEach(likedStore -> {
//            Store store = storeRepository.findById(likedStore.getStoreId()).get();
//            double rateAvg = calculateRate(store);
//            dtoList.add(ResponseStore.GetLikedStoreDto.toDto(store, rateAvg));
//        });
//        return dtoList;
//    }
//
//
//
//    public List<ResponseStore.GetAllStoreDto> getStoreByCategory(StoreSearchCriteria criteria, String category) {
//        //List<Store> entityList = storeRepository.findAllByCategory(category);
//        List<Store> entityList = storeRepository.findStoresByCategory(criteria, category);
//        List<ResponseStore.GetAllStoreDto> dtoList = new ArrayList<>();
//        entityList.stream().forEach(store -> {
//            double rateAvg = calculateRate(store);
//            dtoList.add(ResponseStore.GetAllStoreDto.toDto(store, rateAvg));
//        });
//        return dtoList;
//    }
//
//    public List<ResponseStore.GetByDistanceDto> getStoreByDistance(StoreSearchCriteria criteria){
//        List<AbstractMap.SimpleEntry<Store,Double>> entityList = storeRepository.findStoresByDistance(criteria);
//        List<ResponseStore.GetByDistanceDto> dtoList = new ArrayList<>();
//        entityList.forEach(entry -> {
//            Store store = entry.getKey();
//            double distance = entry.getValue() * 1000.0;
//            if (isStoreOpen(store)) {
//                double rateAvg = calculateRate(store);
//                dtoList.add(ResponseStore.GetByDistanceDto.toDto(store, rateAvg, distance));
//            }
//        });
//        return dtoList;
//    }
//
//    public List<ResponseStore.GetAllStoreDto> getByRecentVisitStore() {
//        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
//        List<Review> reviewList = reviewRepository.findAllByWriterId(userId);
//        List<Store> entityList = reviewList.stream()
//                .map(Review::getStore)
//                .collect(Collectors.toList());
//        List<ResponseStore.GetAllStoreDto> dtoList = new ArrayList<>();
//        entityList.stream().forEach(store -> {
//            double rateAvg = calculateRate(store);
//            dtoList.add(ResponseStore.GetAllStoreDto.toDto(store, rateAvg));
//        });
//        return dtoList;
//
//    }


}
