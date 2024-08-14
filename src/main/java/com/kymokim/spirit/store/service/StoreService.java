package com.kymokim.spirit.store.service;

import com.kymokim.spirit.auth.repository.AuthRepository;
import com.kymokim.spirit.auth.security.JwtAuthToken;
import com.kymokim.spirit.auth.security.JwtAuthTokenProvider;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.review.repository.ReviewRepository;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.dto.StoreSearchCriteria;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.LikedStore;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.repository.LikedStoreRepository;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class StoreService {
    private final StoreRepository storeRepository;
    private final LikedStoreRepository likedStoreRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;
    private final S3Service s3Service;
    private final ReviewRepository reviewRepository;

    public Long createStore(RequestStore.CreateStoreDto createStoreDto, Optional<String> token) {

        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long writerId = authRepository.findByEmail(email).getId();
        Store store = RequestStore.CreateStoreDto.toEntity(createStoreDto, writerId);
        storeRepository.save(store);
        return store.getStoreId();
    }


    public String uploadImg(MultipartFile file, long storeId){
        Store store = storeRepository.findById(storeId).get();

//        if (!store.getImgUrl().isEmpty())
//            s3Service.deleteFile(store.getImgUrl());

        String url = "";
        try {
            url = s3Service.upload(file,"store");
        }
        catch (IOException e){
            System.out.println("S3 upload failed.");
        }

        store.setImgUrl(url);
        storeRepository.save(store);
        return url;
    }

    public void likeStore(Long storeId, Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
        LikedStore likedStore = LikedStore.builder()
                .storeId(storeId)
                .userId(userId)
                .build();
        likedStoreRepository.save(likedStore);
        Store store = storeRepository.findById(storeId).get();
        store.increaseStoreLikeCount();
        storeRepository.save(store);
    }

    public void unlikeStore(Long storeId, Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
        LikedStore likedStore = likedStoreRepository.findByUserIdAndStoreId(userId, storeId);
        likedStoreRepository.delete(likedStore);
        Store store = storeRepository.findById(storeId).get();
        store.decreaseStoreLikeCount();
        storeRepository.save(store);
    }

    public List<ResponseStore.GetAllStoreDto> getAllStore() {
        List<Store> entityList = storeRepository.findAll();
        List<ResponseStore.GetAllStoreDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(store -> {
            Double rateAvg = store.getTotalRate() / store.getReviewCount();
            rateAvg = Math.round(rateAvg * 100.0) / 100.0;
            dtoList.add(ResponseStore.GetAllStoreDto.toDto(store, rateAvg));
        });
        return dtoList;
    }

    public List<ResponseStore.GetLikedStoreDto> getLikedStore(Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
        List<LikedStore> entityList = likedStoreRepository.findAllByUserId(userId);
        List<ResponseStore.GetLikedStoreDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(likedStore -> {
            Store store = storeRepository.findById(likedStore.getStoreId()).get();
            Double rateAvg = store.getTotalRate() / store.getReviewCount();
            rateAvg = Math.round(rateAvg * 100.0) / 100.0;
            dtoList.add(ResponseStore.GetLikedStoreDto.toDto(store, rateAvg));
        });
        return dtoList;
    }

    public ResponseStore.GetStoreDto getStore(Long storeId, Optional<String> token) {
        Store store = storeRepository.findById(storeId).get();
        Double rateAvg = store.getTotalRate() / store.getReviewCount();
        rateAvg = Math.round(rateAvg * 100.0) / 100.0;

        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
        boolean isStoreLiked = false;
        LikedStore likedStore = likedStoreRepository.findByUserIdAndStoreId(userId, storeId);
        if (likedStore != null)
            isStoreLiked = true;
        return ResponseStore.GetStoreDto.toDto(store, rateAvg, isStoreLiked, isStoreOpen(store));
    }

    public List<ResponseStore.GetAllStoreDto> getStoreByCategory(StoreSearchCriteria criteria, String category) {
        //List<Store> entityList = storeRepository.findAllByCategory(category);
        List<Store> entityList = storeRepository.findStoresByCategory(criteria, category);
        List<ResponseStore.GetAllStoreDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(store -> {
            Double rateAvg = store.getTotalRate() / store.getReviewCount();
            rateAvg = Math.round(rateAvg * 100.0) / 100.0;
            dtoList.add(ResponseStore.GetAllStoreDto.toDto(store, rateAvg));
        });
        return dtoList;
    }

    public List<ResponseStore.GetAllStoreDto> getStoreByDistance(StoreSearchCriteria criteria){
        List<Store> entityList = storeRepository.findStoresByDistance(criteria);
        List<ResponseStore.GetAllStoreDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(store -> {
            if (isStoreOpen(store)) {
                Double rateAvg = store.getTotalRate() / store.getReviewCount();
                rateAvg = Math.round(rateAvg * 100.0) / 100.0;
                dtoList.add(ResponseStore.GetAllStoreDto.toDto(store, rateAvg));
            }
        });
        return dtoList;
    }

    public List<ResponseStore.GetAllStoreDto> getByWriterStore(Optional<String> token) {
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long writerId = authRepository.findByEmail(email).getId();
        List<Store> entityList = storeRepository.findAllByWriterId(writerId);
        List<ResponseStore.GetAllStoreDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(store -> {
            Double rateAvg = store.getTotalRate() / store.getReviewCount();
            rateAvg = Math.round(rateAvg * 100.0) / 100.0;
            dtoList.add(ResponseStore.GetAllStoreDto.toDto(store, rateAvg));
        });
        return dtoList;
    }

    public List<ResponseStore.GetAllStoreDto> getByRecentVisitStore(Optional<String> token) {
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long writerId = authRepository.findByEmail(email).getId();
        List<Review> reviewList = reviewRepository.findAllByWriterId(writerId);
        List<Store> entityList = reviewList.stream()
                .map(Review::getStore)
                .collect(Collectors.toList());
        List<ResponseStore.GetAllStoreDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(store -> {
            Double rateAvg = store.getTotalRate() / store.getReviewCount();
            rateAvg = Math.round(rateAvg * 100.0) / 100.0;
            dtoList.add(ResponseStore.GetAllStoreDto.toDto(store, rateAvg));
        });
        return dtoList;

    }

    public boolean isStoreOpen(Store store) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek today = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        // 휴무일에 포함되어 있는지 확인
        if (store.getClosedDays().contains(today)) {
            return false;
        }

        // 영업 시간 확인
        LocalTime openHour = store.getOpenHour();
        LocalTime closeHour = store.getCloseHour();

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


    public void updateStore(RequestStore.UpdateStoreDto updateStoreDto) {
        Store originalStore = storeRepository.findById(updateStoreDto.getStoreId()).get();
        Store updatedStore = RequestStore.UpdateStoreDto.toEntity(originalStore, updateStoreDto);
        storeRepository.save(updatedStore);
    }

    //Delete permission exception handling required.
    public void deleteStore(Long storeId) {
        Store store = storeRepository.findById(storeId).get();
        storeRepository.delete(store);
    }
}
