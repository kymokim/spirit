package com.kymokim.spirit.store.service;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.store.dto.CommonStore;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.entity.LikedStore;
import com.kymokim.spirit.store.entity.MainDrink;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.entity.StoreImage;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.LikedStoreRepository;
import com.kymokim.spirit.store.repository.StoreImageRepository;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreImageRepository storeImageRepository;
    private final LikedStoreRepository likedStoreRepository;
    private final S3Service s3Service;

    private Long resolveUserId(){
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private Store resolveStore(Long storeId){
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    @Transactional
    public ResponseStore.CreateStoreRsDto createStore(MultipartFile[] files, RequestStore.CreateStoreRqDto createStoreRqDto) {
        Store store = createStoreRqDto.toEntity(resolveUserId());
        storeRepository.save(store);
        if (files != null) {
            List<MultipartFile> fileList = Arrays.asList(files);
            List<String> imageUrls = s3Service.uploadMultiple(fileList, "store/" + String.valueOf(store.getId()));
            store.setMainImgUrl(imageUrls.getFirst());
            for (String url : imageUrls){
                StoreImage storeImage = StoreImage.builder().url(url).store(store).build();
                storeImageRepository.save(storeImage);
                store.addImgUrlList(storeImage);
            }
            storeRepository.save(store);
        }
        return ResponseStore.CreateStoreRsDto.toDto(store);
    }

    @Transactional
    public void updateStore(Long storeId, RequestStore.UpdateStoreDto updateStoreDto) {
        Store store = resolveStore(storeId);

        updateIfNotNullOrEmpty(updateStoreDto.getMainImgUrl(), store::setMainImgUrl);
        updateIfNotNullOrEmpty(updateStoreDto.getName(), store::setName);
        updateIfNotNullOrEmpty(updateStoreDto.getContact(), store::setContact);
        updateIfNotNullOrEmpty(updateStoreDto.getDescription(), store::setDescription);
        updateIfNotNullOrEmpty(updateStoreDto.getHasScreen(), store::setHasScreen);
        updateIfNotNullOrEmpty(updateStoreDto.getIsGroupAvailable(), store::setIsGroupAvailable);
        updateIfNotNullOrEmpty(updateStoreDto.getLocationDto(), locationDto -> store.setLocation(locationDto.toEntity()));
        updateIfNotNullOrEmpty(updateStoreDto.getBusinessHoursDto(), businessHoursDto -> store.setBusinessHours(businessHoursDto.toEntity()));
        updateIfNotNullOrEmpty(updateStoreDto.getCategories(), store::setCategories);
        updateIfNotNullOrEmpty(updateStoreDto.getMainDrinkDtos(), mainDrinkDtos -> {
            Set<MainDrink> mainDrinks = mainDrinkDtos.stream().map(CommonStore.MainDrinkDto::toEntity).collect(Collectors.toSet());
            store.setMainDrinks(mainDrinks);
        });
        updateIfNotNullOrEmpty(updateStoreDto.getClosedDays(), store::setClosedDays);

        store.getHistoryInfo().update(resolveUserId());
        storeRepository.save(store);
    }

    private <T> void updateIfNotNullOrEmpty(T value, Consumer<T> updater) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) return;
            updater.accept(value);
        }
    }

    @Transactional
    public ResponseStore.UploadImageDto uploadImage(MultipartFile[] files, Long storeId) {
        Store store = resolveStore(storeId);
        if (files != null) {
            List<MultipartFile> fileList = Arrays.asList(files);
            List<String> imageUrls = s3Service.uploadMultiple(fileList, "store/" + String.valueOf(store.getId()));
            for (String url : imageUrls){
                StoreImage storeImage = StoreImage.builder().url(url).store(store).build();
                storeImageRepository.save(storeImage);
                store.addImgUrlList(storeImage);
            }
            store.getHistoryInfo().update(resolveUserId());
        } else {
            throw new CustomException(StoreErrorCode.STORE_IMG_FILE_EMPTY);
        }
        store.getHistoryInfo().update(resolveUserId());
        storeRepository.save(store);
        List<String> urlList = store.getImgUrlList().stream().map(StoreImage::getUrl).toList();
        return ResponseStore.UploadImageDto.toDto(urlList);
    }

    @Transactional
    public void likeStore(Long storeId){
        Store store = resolveStore(storeId);
        Long userId = resolveUserId();
        LikedStore likedStore = likedStoreRepository.findByUserIdAndStoreId(userId, store.getId());
        if (likedStore == null) {
            likedStore = LikedStore.builder().storeId(store.getId()).userId(userId).build();
            likedStoreRepository.save(likedStore);
            store.increaseLikeCount();
        } else {
            likedStoreRepository.delete(likedStore);
            store.decreaseLikeCount();
        }
        storeRepository.save(store);
    }

    @Transactional
    public void deleteImage(RequestStore.DeleteImageDto deleteImageDto, Long storeId){
        Store store = resolveStore(storeId);
        for (String imgUrl : deleteImageDto.getImgUrlList()) {
            StoreImage storeImage = storeImageRepository.findByUrl(imgUrl)
                            .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_ORIGIN_IMG_URL_EMPTY));
            if (Objects.equals(store.getMainImgUrl(), imgUrl)) {
                store.setMainImgUrl(null);
            }
            s3Service.deleteFile(imgUrl);
            storeImageRepository.delete(storeImage);
            store.removeImgUrlList(storeImage);
        }
        storeRepository.save(store);
    }

    //Delete permission exception handling required.
    @Transactional
    public void deleteStore(Long storeId) {
        Store store = resolveStore(storeId);
        storeRepository.delete(store);
    }
}
