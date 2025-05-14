package com.kymokim.spirit.store.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Role;
import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.repository.AuthRepository;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.store.dto.CommonStore;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.entity.*;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.*;
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
    private final OperationInfoRepository operationInfoRepository;
    private final AuthRepository authRepository;
    private final StoreOwnershipRequestRepository storeOwnershipRequestRepository;
    private final StoreManagerRepository storeManagerRepository;
    private final BusinessRegistrationValidator businessRegistrationValidator;

    private Long resolveUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private Store resolveStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private Auth resolveUser() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        Auth user = authRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        return user;
    }

    private OwnershipRequest resolveOwnership(Long ownershipId) {
        return storeOwnershipRequestRepository.findById(ownershipId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.OWNERSHIP_NOT_FOUND));
    }

    @Transactional
    public ResponseStore.CreateStoreRsDto createStore(MultipartFile[] files, RequestStore.CreateStoreRqDto createStoreRqDto) {
        Store store = createStoreRqDto.toEntity(resolveUserId());
        storeRepository.save(store);
        if (files != null) {
            List<MultipartFile> fileList = Arrays.asList(files);
            List<String> imageUrls = s3Service.uploadMultiple(fileList, "store/" + String.valueOf(store.getId()));
            store.setMainImgUrl(imageUrls.getFirst());
            for (String url : imageUrls) {
                StoreImage storeImage = StoreImage.builder().url(url).store(store).build();
                storeImageRepository.save(storeImage);
                store.addImgUrlList(storeImage);
            }
            storeRepository.save(store);
        }
        setIsAlwaysOpenAndOperationInfos(store, createStoreRqDto.getIsAlwaysOpen(), createStoreRqDto.getOperationInfoDtos());
        Store updatedStore = resolveStore(store.getId());
        if (updatedStore.getIsAlwaysOpen() == null && (updatedStore.getOperationInfos() == null || updatedStore.getOperationInfos().isEmpty()))
            throw new CustomException(StoreErrorCode.WRONG_OPERATION_INFO);

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
        updateIfNotNullOrEmpty(updateStoreDto.getCategories(), store::setCategories);
        updateIfNotNullOrEmpty(updateStoreDto.getMainDrinkDtos(), mainDrinkDtos -> {
            Set<MainDrink> mainDrinks = mainDrinkDtos.stream().map(CommonStore.MainDrinkDto::toEntity).collect(Collectors.toSet());
            store.setMainDrinks(mainDrinks);
        });
        if (!Objects.equals(updateStoreDto.getIsAlwaysOpen(), null)) {
            if (Objects.equals(updateStoreDto.getOperationInfoDtos(), null)) {
                setIsAlwaysOpenAndOperationInfos(store, updateStoreDto.getIsAlwaysOpen(), null);
            } else {
                setIsAlwaysOpenAndOperationInfos(store, updateStoreDto.getIsAlwaysOpen(), updateStoreDto.getOperationInfoDtos());
            }
        }

        store.getHistoryInfo().update(resolveUserId());
        storeRepository.save(store);
    }

    private <T> void updateIfNotNullOrEmpty(T value, Consumer<T> updater) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) return;
            updater.accept(value);
        }
    }

    private void setIsAlwaysOpenAndOperationInfos(Store store, Boolean isAlwaysOpen, Set<CommonStore.OperationInfoDto> operationInfoDtos) {
        if (isAlwaysOpen != null) {
            if (Objects.equals(isAlwaysOpen, false) && (!Objects.equals(operationInfoDtos, null) && !operationInfoDtos.isEmpty())) {
                if (store.getOperationInfos() != null && !store.getOperationInfos().isEmpty()) {
                    Set<OperationInfo> currentOperationInfos = new HashSet<>(store.getOperationInfos());
                    operationInfoRepository.deleteAll(currentOperationInfos);
                    store.getOperationInfos().clear();
                }
                store.setIsAlwaysOpen(false);
                operationInfoDtos.forEach(operationInfoDto -> {
                    OperationInfo operationInfo = operationInfoDto.toEntity(store);
                    operationInfoRepository.save(operationInfo);
                    store.addOperationInfos(operationInfo);
                });
                storeRepository.save(store);
            } else if (Objects.equals(isAlwaysOpen, true) && (Objects.equals(operationInfoDtos, null))) {
                if (store.getOperationInfos() != null && !store.getOperationInfos().isEmpty()) {
                    Set<OperationInfo> currentOperationInfos = new HashSet<>(store.getOperationInfos());
                    operationInfoRepository.deleteAll(currentOperationInfos);
                    store.getOperationInfos().clear();
                }
                store.setIsAlwaysOpen(true);
                storeRepository.save(store);
            } else throw new CustomException(StoreErrorCode.WRONG_OPERATION_INFO);
        }
    }

    @Transactional
    public ResponseStore.ImageListDto uploadImage(MultipartFile[] files, Long storeId) {
        Store store = resolveStore(storeId);
        if (files != null) {
            List<MultipartFile> fileList = Arrays.asList(files);
            List<String> imageUrls = s3Service.uploadMultiple(fileList, "store/" + String.valueOf(store.getId()));
            for (String url : imageUrls) {
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
        return ResponseStore.ImageListDto.toDto(urlList);
    }

    @Transactional
    public void likeStore(Long storeId) {
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
    public ResponseStore.ImageListDto deleteImage(RequestStore.DeleteImageDto deleteImageDto, Long storeId) {
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
        List<String> urlList = store.getImgUrlList().stream().map(StoreImage::getUrl).toList();
        return ResponseStore.ImageListDto.toDto(urlList);
    }

    //TODO 매장 운영자 추가 시 소유자만 삭제 가능하게도 추가
    @Transactional
    public void deleteStore(Long storeId) {
        Store store = resolveStore(storeId);
        if (!Objects.equals(store.getImgUrlList(), null) && !store.getImgUrlList().isEmpty()) {
            List<StoreImage> toDelete = new ArrayList<>(store.getImgUrlList());
            for (StoreImage storeImage : toDelete) {
                s3Service.deleteFile(storeImage.getUrl());
                storeImageRepository.delete(storeImage);
                store.removeImgUrlList(storeImage);
            }
        }
        store.delete();
        List<LikedStore> likedStoreList = likedStoreRepository.findAllByStoreId(storeId);
        if (likedStoreList != null) {
            likedStoreList.forEach(likedStoreRepository::delete);
        }
        storeRepository.save(store);
    }


    @Transactional
    public void createOwnership(MultipartFile file, RequestStore.CreateOwnershipRqDto createOwnershipRqDto) {
        Store store = resolveStore(createOwnershipRqDto.getStoreId());
        Auth requester = resolveUser();


        //TODO 이미 인증된 매장 신청 불가 기능 추가
        //TODO 중복 신청 방지 기능 활성화 예정
//        if (storeOwnershipRqRepository
//                .existsByRequesterAndStore(requester, store)) {
//            throw new CustomException(StoreErrorCode.OWNERSHIP_ALREADY_REQUESTED);
//        }

        List<RepresentativeInfo> reps = createOwnershipRqDto.getRepresentativeInfoList();

        boolean hasMainRep = reps != null && reps.stream()
                .anyMatch(rep -> Boolean.TRUE.equals(rep.getIsMainRep()));

        if (!hasMainRep) {
            throw new CustomException(StoreErrorCode.OWNERSHIP_MAIN_REPRESENTATIVE_MISSING);
        }
        boolean isNameMatched = reps.stream()
                .map(RepresentativeInfo::getName)
                .anyMatch(name -> name.equals(requester.getPersonalInfo().getName()));
        if (!isNameMatched) {
            throw new CustomException(StoreErrorCode.OWNERSHIP_REQUESTER_NAME_NOT_MATCHED);
        }

        boolean validated = businessRegistrationValidator.validateBusiness(
                createOwnershipRqDto.getBusinessRegistrationNumber(),
                reps.stream().filter(rep -> Boolean.TRUE.equals(rep.getIsMainRep()))
                        .findFirst()
                        .map(RepresentativeInfo::getName)
                        .orElse(null), // 대표자 이름이 없을 수도 있으니 null 처리
                createOwnershipRqDto.getOpeningDate()
        );
        if (!validated) {
            throw new CustomException(StoreErrorCode.BUSINESS_REGISTRATION_VALIDATION_FAILED);
        }

// 대표자 목록 중 신청자 이름이 있는지 검사


        OwnershipRequest ownershipRequest = createOwnershipRqDto.toEntity(store, requester);
        storeOwnershipRequestRepository.save(ownershipRequest);
        if (file != null && !file.isEmpty()) {
            String imageUrl = s3Service.upload(file, "store/ownership/" + ownershipRequest.getId());
            ownershipRequest.setBusinessRegistrationCertificateImgUrl(imageUrl);
        }
    }

    @Transactional
    public void approveOwnership(Long ownershipId) {
        OwnershipRequest ownershipRequest = resolveOwnership(ownershipId);
        StoreManager storeManager = storeManagerRepository.findByUserIdAndStoreId(ownershipRequest.getRequester().getId(), ownershipRequest.getStore().getId());
        if (ownershipRequest.getStore().getOwnerId() != null || storeManager != null) {
            throw new CustomException(StoreErrorCode.STORE_OWNER_ALREADY_EXIST);
        }

        ownershipRequest.getStore().setOwnerId(ownershipRequest.getRequester().getId());

        ownershipRequest.getRequester().getRoles().add(Role.MANAGER);
        storeOwnershipRequestRepository.delete(ownershipRequest);

        storeManager = StoreManager.builder().storeId(ownershipRequest.getStore().getId()).userId(ownershipRequest.getRequester().getId()).build();
        storeManagerRepository.save(storeManager);

        //TODO 신청자에게 승인 알림 기능 추가
    }

    @Transactional
    public void rejectOwnership(Long ownershipId) {
        OwnershipRequest ownershipRequest = resolveOwnership(ownershipId);

        String certImageUrl = ownershipRequest.getBusinessRegistrationCertificateImgUrl();
        if (certImageUrl != null && !certImageUrl.isBlank()) {
            s3Service.deleteFile(certImageUrl);
        }

        storeOwnershipRequestRepository.delete(ownershipRequest);

        //TODO 신청자에게 거절 알림 기능 추가

    }




}
