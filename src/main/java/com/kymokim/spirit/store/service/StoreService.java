package com.kymokim.spirit.store.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Role;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.auth.service.AuthService;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.AESUtil;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.link.dto.LinkData;
import com.kymokim.spirit.link.dto.PathType;
import com.kymokim.spirit.link.service.LinkBuilder;
import com.kymokim.spirit.notification.dto.NotificationEvent;
import com.kymokim.spirit.notification.dto.store.*;
import com.kymokim.spirit.store.dto.CommonStore;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.entity.*;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.*;
import com.kymokim.spirit.log.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@MainTransactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreImageRepository storeImageRepository;
    private final LikedStoreRepository likedStoreRepository;
    private final S3Service s3Service;
    private final OperationInfoRepository operationInfoRepository;
    private final OwnershipRequestRepository ownershipRequestRepository;
    private final StoreManagerRepository storeManagerRepository;
    private final BusinessRegistrationValidator businessRegistrationValidator;
    private final AESUtil aesUtil;
    private final StoreSuggestionRepository storeSuggestionRepository;
    private final LogService logService;
    private final BoardImageRepository boardImageRepository;
    private final AuthService authService;
    private final LinkBuilder linkBuilder;
    private final ManagerInvitationRepository managerInvitationRepository;

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

    public ResponseStore.CreateStoreRsDto createStore(MultipartFile[] files, RequestStore.CreateStoreRqDto createStoreRqDto) {
        Store store = createStoreRqDto.toEntity(AuthResolver.resolveUserId());
        storeRepository.save(store);
        if (files != null) {
            List<MultipartFile> fileList = Arrays.asList(files);
            List<String> imageUrls = s3Service.uploadMultiple(fileList, "store/" + String.valueOf(store.getId()));
            store.setMainImgUrl(imageUrls.getFirst());
            for (int i = 0; i < imageUrls.size(); i++) {
                StoreImage storeImage = StoreImage.builder().url(imageUrls.get(i)).sortOrder(i).store(store).build();
                storeImageRepository.save(storeImage);
                store.addImgUrlList(storeImage);
            }
            storeRepository.save(store);
        }
        if (createStoreRqDto.getIsAlwaysOpen() == null && (createStoreRqDto.getOperationInfoDtos() == null || createStoreRqDto.getOperationInfoDtos().isEmpty()))
            throw new CustomException(StoreErrorCode.WRONG_OPERATION_INFO);
        setIsAlwaysOpenAndOperationInfos(store, createStoreRqDto.getIsAlwaysOpen(), createStoreRqDto.getOperationInfoDtos());

        return ResponseStore.CreateStoreRsDto.toDto(store);
    }

    public void suggestStore(MultipartFile[] files, RequestStore.SuggestStoreDto suggestStoreDto) {
        Auth user = AuthResolver.resolveUser();
        Store store = suggestStoreDto.toEntity(user.getId());
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
        if (!(suggestStoreDto.getIsAlwaysOpen() == null && (suggestStoreDto.getOperationInfoDtos() == null || suggestStoreDto.getOperationInfoDtos().isEmpty())))
            setIsAlwaysOpenAndOperationInfos(store, suggestStoreDto.getIsAlwaysOpen(), suggestStoreDto.getOperationInfoDtos());

        StoreSuggestion storeSuggestion = StoreSuggestion.builder().store(store).suggesterId(user.getId()).build();
        storeSuggestionRepository.save(storeSuggestion);
        NotificationEvent.raise(new StoreSuggestionCreatedNotificationEvent(store));
    }

    public void approveStoreSuggestion(Long storeSuggestionId) {
        StoreSuggestion storeSuggestion = storeSuggestionRepository.findById(storeSuggestionId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_SUGGESTION_NOT_FOUND));
        Store store = storeSuggestion.getStore();
        FacilitiesInfo facilitiesInfo = store.getFacilitiesInfo();
        if (facilitiesInfo == null
                || facilitiesInfo.getHasScreen() == null
                || facilitiesInfo.getHasRoom() == null
                || facilitiesInfo.getIsGroupAvailable() == null
                || facilitiesInfo.getIsParkingAvailable() == null
                || facilitiesInfo.getIsCorkageAvailable() == null) {
            throw new CustomException(StoreErrorCode.FACILITIES_INFO_EMPTY);
        }
        if (Objects.equals(store.getIsAlwaysOpen(), null)
                || Objects.equals(store.getCategories(), null)
                || store.getCategories().isEmpty()
                || Objects.equals(store.getMainDrinks(), null)
                || store.getMainDrinks().isEmpty()) {
            throw new CustomException(StoreErrorCode.STORE_REQUIRED_INFO_EMPTY);
        }
        store.setIsDeleted(false);
        storeRepository.save(store);
        storeSuggestionRepository.delete(storeSuggestion);
    }

    public void rejectStoreSuggestion(Long storeSuggestionId) {
        StoreSuggestion storeSuggestion = storeSuggestionRepository.findById(storeSuggestionId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_SUGGESTION_NOT_FOUND));
        Store store = storeSuggestion.getStore();
        storeSuggestionRepository.delete(storeSuggestion);
        deleteStore(store.getId());
    }

    public void createStoreWithOwnership(MultipartFile[] storeImages, MultipartFile businessRegistrationCertificateImage, RequestStore.CreateStoreWithOwnershipRqDto createStoreWithOwnershipRqDto) {
        RequestStore.CreateStoreRqDto createStoreRqDto = createStoreWithOwnershipRqDto.getCreateStoreRqDto();
        Long storeId = createStore(storeImages, createStoreRqDto).getId();
        Store store = resolveStore(storeId);
        store.delete();
        StoreSuggestion storeSuggestion = StoreSuggestion.builder().store(store).suggesterId(AuthResolver.resolveUserId()).build();
        storeSuggestionRepository.save(storeSuggestion);

        RequestStore.CreateOwnershipRqDto createOwnershipRqDto = createStoreWithOwnershipRqDto.getCreateOwnershipRqDto();
        createOwnershipRqDto.setStoreId(storeId);
        createOwnership(businessRegistrationCertificateImage, createOwnershipRqDto);
        NotificationEvent.raise(new StoreOwnershipRequestCreatedNotificationEvent(store));
    }

    public void updateStore(Long storeId, RequestStore.UpdateStoreDto updateStoreDto) {
        validateStoreAccess(storeId);
        Store store = resolveStore(storeId);

        updateIfNotNullOrEmpty(updateStoreDto.getMainImgUrl(), store::setMainImgUrl);
        updateIfNotNullOrEmpty(updateStoreDto.getName(), store::setName);
        updateIfNotNullOrEmpty(updateStoreDto.getContact(), store::setContact);
        updateIfNotNullOrEmpty(updateStoreDto.getDescription(), store::setDescription);
        updateIfNotNullOrEmpty(updateStoreDto.getFacilitiesInfoDto(), facilitiesInfoDto -> store.setFacilitiesInfo(facilitiesInfoDto.toEntity()));
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

        store.getHistoryInfo().update(AuthResolver.resolveUserId());
        storeRepository.save(store);
    }

    public ResponseStore.ImageListDto updateStoreImageSortOrder(RequestStore.UpdateStoreImageSortOrderDto updateStoreImageSortOrderDto) {
        validateStoreAccess(updateStoreImageSortOrderDto.getStoreId());
        List<String> storeImageUrlInOrderList = updateStoreImageSortOrderDto.getStoreImageUrlInOrderList();
        List<StoreImage> images = storeImageRepository.findAllByUrlIn(storeImageUrlInOrderList);

        for (int i = 0; i < storeImageUrlInOrderList.size(); i++) {
            String imageUrl = storeImageUrlInOrderList.get(i);
            StoreImage image = images.stream()
                    .filter(img -> img.getUrl().equals(imageUrl))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_IMAGE_NOT_FOUND));

            if (!image.getStore().getId().equals(updateStoreImageSortOrderDto.getStoreId())) {
                throw new CustomException(StoreErrorCode.INVALID_STORE_IMAGE_RELATION);
            }
            image.setSortOrder(i);
        }
        storeImageRepository.flush();
        Store store = resolveStore(updateStoreImageSortOrderDto.getStoreId());
        List<String> urlList = store.getImgUrlList().stream().map(StoreImage::getUrl).toList();
        return ResponseStore.ImageListDto.toDto(urlList);
    }

    public List<ResponseStore.BoardImageListDto> updateBoardImageSortOrder(RequestStore.UpdateBoardImageSortOrderDto updateBoardImageSortOrderDto) {
        validateStoreAccess(updateBoardImageSortOrderDto.getStoreId());
        List<String> boardImageUrlInOrderList = updateBoardImageSortOrderDto.getBoardImageUrlInOrderList();
        List<BoardImage> images = boardImageRepository.findAllByUrlIn(boardImageUrlInOrderList);

        for (int i = 0; i < boardImageUrlInOrderList.size(); i++) {
            String imageUrl = boardImageUrlInOrderList.get(i);
            BoardImage image = images.stream()
                    .filter(img -> img.getUrl().equals(imageUrl))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(StoreErrorCode.BOARD_IMAGE_NOT_FOUND));

            if (!image.getStore().getId().equals(updateBoardImageSortOrderDto.getStoreId())) {
                throw new CustomException(StoreErrorCode.INVALID_STORE_BOARD_IMAGE_RELATION);
            }
            image.setSortOrder(i);
        }
        boardImageRepository.flush();
        Store store = resolveStore(updateBoardImageSortOrderDto.getStoreId());
        return store.getBoardImgUrlList().stream().map(ResponseStore.BoardImageListDto::toDto).toList();
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

    public ResponseStore.ImageListDto uploadImage(MultipartFile[] files, Long storeId) {
        validateStoreAccess(storeId);
        Store store = resolveStore(storeId);

        if (files == null || files.length == 0) {
            throw new CustomException(StoreErrorCode.STORE_IMG_FILE_EMPTY);
        }

        List<MultipartFile> fileList = Arrays.asList(files);
        List<String> imageUrls = s3Service.uploadMultiple(fileList, "store/" + String.valueOf(store.getId()));
        int maxOrder = storeImageRepository.findMaxSortOrderByStoreId(storeId).orElse(-1);
        for (int i = 0; i < imageUrls.size(); i++) {
            int sortOrder = maxOrder + i + 1;
            StoreImage storeImage = StoreImage.builder().url(imageUrls.get(i)).store(store).sortOrder(sortOrder).build();
            storeImageRepository.save(storeImage);
            store.addImgUrlList(storeImage);
        }
        store.getHistoryInfo().update(AuthResolver.resolveUserId());
        storeRepository.save(store);
        List<String> urlList = store.getImgUrlList().stream().map(StoreImage::getUrl).toList();
        return ResponseStore.ImageListDto.toDto(urlList);
    }

    public List<ResponseStore.BoardImageListDto> uploadBoardImage(MultipartFile[] files, Long storeId, BoardType boardType) {
        validateStoreAccess(storeId);
        Store store = resolveStore(storeId);

        if (files == null || files.length == 0) {
            throw new CustomException(StoreErrorCode.BOARD_IMG_FILE_EMPTY);
        }

        List<MultipartFile> fileList = Arrays.asList(files);
        List<String> imageUrls = s3Service.uploadMultiple(fileList, "store/" + String.valueOf(store.getId()) + "/board");
        int maxOrder = boardImageRepository.findMaxSortOrderByStoreId(storeId).orElse(-1);
        for (int i = 0; i < imageUrls.size(); i++) {
            int sortOrder = maxOrder + i + 1;
            BoardImage boardImage = BoardImage.builder().url(imageUrls.get(i)).store(store).sortOrder(sortOrder).boardType(boardType).build();
            boardImageRepository.save(boardImage);
            store.addBoardImgUrlList(boardImage);
        }
        store.getHistoryInfo().update(AuthResolver.resolveUserId());
        storeRepository.save(store);

        return store.getBoardImgUrlList().stream().map(ResponseStore.BoardImageListDto::toDto).toList();
    }

    public void updateBoardImageType(Long boardImageId, BoardType boardType) {
        BoardImage boardImage = boardImageRepository.findById(boardImageId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.BOARD_IMAGE_NOT_FOUND));
        validateStoreAccess(boardImage.getStore().getId());
        boardImage.setBoardType(boardType);
        boardImageRepository.save(boardImage);
    }

    public void likeStore(Long storeId) {
        Store store = resolveStore(storeId);
        Auth user = AuthResolver.resolveUser();
        LikedStore likedStore = likedStoreRepository.findByUserIdAndStoreId(user.getId(), store.getId());
        if (likedStore == null) {
            likedStore = LikedStore.builder()
                    .storeId(store.getId())
                    .userId(user.getId())
                    .gender(user.getPersonalInfo().getGender())
                    .birthYear(aesUtil.decrypt(user.getPersonalInfo().getBirthDate()).substring(0, 4))
                    .build();
            likedStoreRepository.save(likedStore);
            store.increaseLikeCount();
        } else {
            likedStoreRepository.delete(likedStore);
            store.decreaseLikeCount();
        }
        storeRepository.save(store);
    }

    public ResponseStore.ImageListDto deleteImage(RequestStore.DeleteImageDto deleteImageDto, Long storeId) {
        validateStoreAccess(storeId);
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

    public List<ResponseStore.BoardImageListDto> deleteBoardImage(RequestStore.DeleteImageDto deleteImageDto, Long storeId) {
        validateStoreAccess(storeId);
        Store store = resolveStore(storeId);
        for (String imgUrl : deleteImageDto.getImgUrlList()) {
            BoardImage boardImage = boardImageRepository.findByUrl(imgUrl)
                    .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_ORIGIN_IMG_URL_EMPTY));
            s3Service.deleteFile(imgUrl);
            boardImageRepository.delete(boardImage);
            store.removeBoardImgUrlList(boardImage);
        }
        storeRepository.save(store);

        return store.getBoardImgUrlList().stream().map(ResponseStore.BoardImageListDto::toDto).toList();
    }

    public void deleteStore(Long storeId) {
        Store store = resolveStore(storeId);
        Auth user = AuthResolver.resolveUser();
        if (!user.getRoles().contains(Role.ADMIN) && !Objects.equals(store.getOwnerId(), user.getId())) {
            throw new CustomException(StoreErrorCode.STORE_UNAUTHORIZED_ACCESS);
        }
        List<StoreManager> storeManagerList = storeManagerRepository.findAllByStoreId(storeId);
        storeManagerRepository.deleteAll(storeManagerList);
        if (!Objects.equals(store.getImgUrlList(), null) && !store.getImgUrlList().isEmpty()) {
            List<StoreImage> toDelete = new ArrayList<>(store.getImgUrlList());
            for (StoreImage storeImage : toDelete) {
                s3Service.deleteFile(storeImage.getUrl());
                storeImageRepository.delete(storeImage);
                store.removeImgUrlList(storeImage);
            }
        }
        if (!Objects.equals(store.getBoardImgUrlList(), null) && !store.getBoardImgUrlList().isEmpty()) {
            List<BoardImage> toDelete = new ArrayList<>(store.getBoardImgUrlList());
            for (BoardImage boardImage : toDelete) {
                s3Service.deleteFile(boardImage.getUrl());
                boardImageRepository.delete(boardImage);
                store.removeBoardImgUrlList(boardImage);
            }
        }
        store.delete();
        authService.removeRole(user, Role.MANAGER);
        List<LikedStore> likedStoreList = likedStoreRepository.findAllByStoreId(storeId);
        if (likedStoreList != null) {
            likedStoreList.forEach(likedStoreRepository::delete);
        }
        if (store.getMainImgUrl() != null) {
            store.setMainImgUrl(null);
        }
        storeRepository.save(store);
    }

    public void createOwnership(MultipartFile file, RequestStore.CreateOwnershipRqDto createOwnershipRqDto) {
        Store store = resolveStore(createOwnershipRqDto.getStoreId());
        Auth requester = AuthResolver.resolveUser();

        if (store.getOwnerId() != null) {
            throw new CustomException(StoreErrorCode.STORE_OWNER_ALREADY_EXIST);
        }

        if (ownershipRequestRepository.existsByRequesterIdAndStore(requester.getId(), store)) {
            throw new CustomException(StoreErrorCode.OWNERSHIP_ALREADY_REQUESTED);
        }

        List<RepresentativeInfo> reps = createOwnershipRqDto.getRepresentativeInfoList();

        boolean hasMainRep = reps != null && reps.stream()
                .anyMatch(rep -> Boolean.TRUE.equals(rep.getIsMainRep()));

        if (!hasMainRep) {
            throw new CustomException(StoreErrorCode.OWNERSHIP_MAIN_REPRESENTATIVE_MISSING);
        }
        boolean isNameMatched = reps.stream()
                .map(RepresentativeInfo::getName)
                .anyMatch(name -> name.equals(aesUtil.decrypt(requester.getPersonalInfo().getName())));
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

        OwnershipRequest ownershipRequest = createOwnershipRqDto.toEntity(store, requester.getId());
        ownershipRequestRepository.save(ownershipRequest);
        if (file != null && !file.isEmpty()) {
            String imageUrl = s3Service.upload(file, "store/ownership/" + ownershipRequest.getId());
            ownershipRequest.setBusinessRegistrationCertificateImgUrl(imageUrl);
        }
        NotificationEvent.raise(new StoreOwnershipRequestCreatedNotificationEvent(store));
    }

    public void approveOwnership(Long ownershipId) {
        OwnershipRequest ownershipRequest = resolveOwnership(ownershipId);
        Auth requester = AuthResolver.resolveUser(ownershipRequest.getRequesterId());
        StoreManager storeManager = storeManagerRepository.findByUserIdAndStoreId(requester.getId(), ownershipRequest.getStore().getId());
        if (ownershipRequest.getStore().getOwnerId() != null || storeManager != null) {
            throw new CustomException(StoreErrorCode.STORE_OWNER_ALREADY_EXIST);
        }

        ownershipRequest.getStore().setOwnerId(requester.getId());
        authService.addRole(requester, Role.MANAGER);

        ownershipRequestRepository.delete(ownershipRequest);

        storeManager = StoreManager.builder().storeId(ownershipRequest.getStore().getId()).userId(requester.getId()).build();
        storeManagerRepository.save(storeManager);

        logService.createOwnershipLog(ownershipRequest.getStore().getId());

        NotificationEvent.raise(new StoreOwnershipApprovedNotificationEvent(requester, ownershipRequest.getStore()));
    }

    public void rejectOwnership(Long ownershipId, String rejectionReason) {
        OwnershipRequest ownershipRequest = resolveOwnership(ownershipId);

        String certImageUrl = ownershipRequest.getBusinessRegistrationCertificateImgUrl();
        if (certImageUrl != null && !certImageUrl.isBlank()) {
            s3Service.deleteFile(certImageUrl);
        }

        ownershipRequestRepository.delete(ownershipRequest);

        NotificationEvent.raise(new StoreOwnershipRejectedNotificationEvent(AuthResolver.resolveUser(ownershipRequest.getRequesterId()), ownershipRequest.getStore(), rejectionReason));
    }

    public ResponseStore.ShareStoreDto shareStore(Long storeId) {
        LinkData.PathData pathData = LinkData.PathData.builder().type(PathType.STORE).id(storeId.toString()).build();
        return ResponseStore.ShareStoreDto.builder().shareLink(linkBuilder.serverLink(pathData)).build();
    }

    public ResponseStore.InviteStoreManagerDto inviteStoreManager(Long storeId) {
        validateStoreAccess(storeId);
        Store store = resolveStore(storeId);
        ManagerInvitation managerInvitation = ManagerInvitation.builder()
                .storeId(storeId)
                .storeName(store.getName())
                .storeImage(!Objects.equals(store.getMainImgUrl(), null) ? store.getMainImgUrl() : null)
                .build();
        managerInvitationRepository.save(managerInvitation);
        LinkData.PathData pathData = LinkData.PathData.builder().type(PathType.STORE_MANAGER_INVITE).id(managerInvitation.getId()).build();
        return ResponseStore.InviteStoreManagerDto.builder().inviteLink(linkBuilder.serverLink(pathData)).build();
    }

    public void acceptManagerInvitation(String invitationId) {
        ManagerInvitation managerInvitation = managerInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.INVALID_MANAGER_INVITATION_CODE));
        if (managerInvitation.getExpiresAt() != null && managerInvitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(StoreErrorCode.INVALID_MANAGER_INVITATION_CODE);
        }
        Auth user = AuthResolver.resolveUser();
        if (storeManagerRepository.existsByUserIdAndStoreId(user.getId(), managerInvitation.getStoreId())) {
            throw new CustomException(StoreErrorCode.STORE_MANAGER_ALREADY_EXIST);
        }
        StoreManager storeManager = StoreManager.builder()
                .storeId(managerInvitation.getStoreId())
                .userId(user.getId())
                .build();
        storeManagerRepository.save(storeManager);
        authService.addRole(user, Role.MANAGER);
        managerInvitationRepository.delete(managerInvitation);

        Store store = resolveStore(storeManager.getStoreId());
        NotificationEvent.raise(new StoreManagerInviteAcceptedNotificationEvent(store));
    }

    public void changeStoreOwner(Long storeManagerId) {
        StoreManager storeManager = storeManagerRepository.findById(storeManagerId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_MANAGER_NOT_FOUND));
        Auth user = AuthResolver.resolveUser();
        boolean isAdmin = user.getRoles().contains(Role.ADMIN);
        Store store = resolveStore(storeManager.getStoreId());
        if (!(isAdmin || Objects.equals(store.getOwnerId(), user.getId()))) {
            throw new CustomException(StoreErrorCode.STORE_UNAUTHORIZED_ACCESS);
        }
        store.setOwnerId(storeManager.getUserId());
        storeRepository.save(store);

        NotificationEvent.raise(new StoreOwnerChangedNotificationEvent(store));
    }

    public void removeStoreManager(Long storeManagerId) {
        StoreManager storeManager = storeManagerRepository.findById(storeManagerId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_MANAGER_NOT_FOUND));
        Auth user = AuthResolver.resolveUser();
        boolean isAdmin = user.getRoles().contains(Role.ADMIN);
        Store store = resolveStore(storeManager.getStoreId());
        if (!(isAdmin || Objects.equals(store.getOwnerId(), user.getId()))) {
            throw new CustomException(StoreErrorCode.STORE_UNAUTHORIZED_ACCESS);
        }
        if (Objects.equals(store.getOwnerId(), storeManager.getUserId())) {
            throw new CustomException(StoreErrorCode.STORE_UNAUTHORIZED_ACCESS, "Owner cannot be removed.");
        }
        Long deletedManagerId = storeManager.getUserId();
        storeManagerRepository.delete(storeManager);
        List<StoreManager> storeManagerList = storeManagerRepository.findAllByUserId(deletedManagerId);
        if (storeManagerList == null || storeManagerList.isEmpty()) {
            authService.removeRole(AuthResolver.resolveUser(deletedManagerId), Role.MANAGER);
        }
    }
}
