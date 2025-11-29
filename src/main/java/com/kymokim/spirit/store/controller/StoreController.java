package com.kymokim.spirit.store.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.entity.BoardType;
import com.kymokim.spirit.store.service.StoreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Store API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createStore(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                                   @Valid @RequestPart(value = "createStoreDto") RequestStore.CreateStoreRqDto createStoreRqDto) {
        ResponseStore.CreateStoreRsDto createStoreRsDto = storeService.createStore(files, createStoreRqDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store created successfully.")
                .data(createStoreRsDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/suggestion/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> suggestStore(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                                    @Valid @RequestPart(value = "suggestStoreDto") RequestStore.SuggestStoreDto suggestStoreDto) {
        ResponseStore.CreateStoreRsDto createStoreRsDto = storeService.suggestStore(files, suggestStoreDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store suggested successfully.")
                .data(createStoreRsDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/suggestion/approve")
    public ResponseEntity<ResponseDto> approveStore(@RequestParam Long storeSuggestionId) {
        storeService.approveStoreSuggestion(storeSuggestionId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store suggestion approved successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping(value = "/suggestion/reject")
    public ResponseEntity<ResponseDto> rejectStore(@RequestParam Long storeSuggestionId) {
        storeService.rejectStoreSuggestion(storeSuggestionId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store suggestion rejected successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Deprecated
    @PostMapping(value = "/create/with-ownership", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createStoreWithOwnership(@RequestPart(value = "storeImages") MultipartFile[] storeImages,
                                                                @RequestPart(value = "businessRegistrationCertificateImage") MultipartFile businessRegistrationCertificateImage,
                                                                @Valid @RequestPart(value = "createStoreWithOwnershipRqDto") RequestStore.CreateStoreWithOwnershipRqDto createStoreWithOwnershipRqDto) {
        storeService.createStoreWithOwnership(storeImages, businessRegistrationCertificateImage, createStoreWithOwnershipRqDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store suggestion and Ownership request created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/create/with-ownership/photo-only", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createStoreWithOwnershipPhotoOnly(@RequestPart(value = "storeImages") MultipartFile[] storeImages,
                                                                         @RequestPart(value = "businessRegistrationCertificateImage") MultipartFile businessRegistrationCertificateImage,
                                                                         @Valid @RequestPart(value = "createStoreRqDto") RequestStore.CreateStoreRqDto createStoreRqDto) {
        storeService.createStoreWithOwnershipPhotoOnly(storeImages, businessRegistrationCertificateImage, createStoreRqDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store suggestion and Ownership photo-only request created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PatchMapping("/update/{storeId}")
    public ResponseEntity<ResponseDto> updateStore(@PathVariable Long storeId, @Valid @RequestBody RequestStore.UpdateStoreDto updateStoreDto) {
        storeService.updateStore(storeId, updateStoreDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update-image/sort-order")
    public ResponseEntity<ResponseDto> updateStoreImageSortOrder(@Valid @RequestBody RequestStore.UpdateStoreImageSortOrderDto updateStoreImageSortOrderDto) {
        ResponseStore.ImageListDto dto = storeService.updateStoreImageSortOrder(updateStoreImageSortOrderDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store image sort order updated successfully.")
                .data(dto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update-board-image/sort-order")
    public ResponseEntity<ResponseDto> updateBoardImageSortOrder(@Valid @RequestBody RequestStore.UpdateBoardImageSortOrderDto updateBoardImageSortOrderDto) {
        List<ResponseStore.BoardImageListDto> dtoList = storeService.updateBoardImageSortOrder(updateBoardImageSortOrderDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Board image sort order updated successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update-board-image/type")
    public ResponseEntity<ResponseDto> updateBoardImageType(@RequestParam Long boardImageId,
                                                            @RequestParam BoardType boardType) {
        storeService.updateBoardImageType(boardImageId, boardType);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Board image type updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/upload-image/{storeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> uploadImage(@RequestPart(value = "files") MultipartFile[] files,
                                                   @PathVariable("storeId") Long storeId) {
        ResponseStore.ImageListDto dto = storeService.uploadImage(files, storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image uploaded successfully.")
                .data(dto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/upload-board-image/{storeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> uploadBoardImage(@RequestPart(value = "files") MultipartFile[] files,
                                                        @RequestParam BoardType boardType,
                                                        @PathVariable("storeId") Long storeId) {
        List<ResponseStore.BoardImageListDto> dtoList = storeService.uploadBoardImage(files, storeId, boardType);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Board image uploaded successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/like/{storeId}")
    public ResponseEntity<ResponseDto> likeStore(@PathVariable("storeId") Long storeId) {
        storeService.likeStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store like processed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete-image/{storeId}")
    public ResponseEntity<ResponseDto> deleteImage(@Valid @RequestBody RequestStore.DeleteImageDto deleteImageDto,
                                                   @PathVariable("storeId") Long storeId) {
        ResponseStore.ImageListDto dto = storeService.deleteImage(deleteImageDto, storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image deleted successfully.")
                .data(dto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete-board-image/{storeId}")
    public ResponseEntity<ResponseDto> deleteBoardImage(@Valid @RequestBody RequestStore.DeleteImageDto deleteImageDto,
                                                        @PathVariable("storeId") Long storeId) {
        List<ResponseStore.BoardImageListDto> dtoList = storeService.deleteBoardImage(deleteImageDto, storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Board image deleted successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<ResponseDto> deleteStore(@PathVariable("storeId") Long storeId) {
        storeService.deleteStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Deprecated
    @PostMapping(value = "/ownership/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createOwnership(@RequestPart(value = "file") MultipartFile file,
                                                       @Valid @RequestPart(value = "createOwnershipRqDto") RequestStore.CreateOwnershipRqDto createOwnershipRqDto) {
        storeService.createOwnership(file, createOwnershipRqDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store ownership created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/ownership/create/photo-only", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createOwnershipPhotoOnly(@RequestPart(value = "file") MultipartFile file,
                                                                @Valid @RequestPart(value = "createOwnershipPhotoOnlyDto") RequestStore.CreateOwnershipPhotoOnlyDto createOwnershipPhotoOnlyDto) {
        storeService.createOwnershipPhotoOnly(file, createOwnershipPhotoOnlyDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store ownership photo-only created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/ownership/business/validate")
    public ResponseEntity<ResponseDto> validateBusiness(@Valid @RequestBody RequestStore.ValidateBusinessDto validateBusinessDto) {
        ResponseStore.BusinessValidationDto dto = storeService.validateBusiness(validateBusinessDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Business validation processed successfully.")
                .data(dto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ownership/approve/{ownershipId}")
    public ResponseEntity<ResponseDto> approveOwnership(@PathVariable("ownershipId") Long ownershipId) {
        storeService.approveOwnership(ownershipId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Ownership approved successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/ownership/reject/{ownershipId}")
    public ResponseEntity<ResponseDto> rejectOwnership(@PathVariable("ownershipId") Long ownershipId,
                                                       @Valid @RequestParam String rejectionReason) {
        storeService.rejectOwnership(ownershipId, rejectionReason);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Ownership deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/share/{storeId}")
    public ResponseEntity<ResponseDto> shareStore(@PathVariable("storeId") Long storeId) {
        ResponseStore.ShareStoreDto shareStoreDto = storeService.shareStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store share link created successfully.")
                .data(shareStoreDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/manager-invitation/create/{storeId}")
    public ResponseEntity<ResponseDto> inviteStoreManager(@PathVariable("storeId") Long storeId) {
        ResponseStore.InviteStoreManagerDto inviteStoreManagerDto = storeService.inviteStoreManager(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store manager invitation created successfully.")
                .data(inviteStoreManagerDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/manager-invitation/accept/{managerInvitationId}")
    public ResponseEntity<ResponseDto> acceptStoreManager(@PathVariable("managerInvitationId") String managerInvitationId) {
        storeService.acceptManagerInvitation(managerInvitationId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store manager invitation accepted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PatchMapping(value = "/owner/change/{storeManagerId}")
    public ResponseEntity<ResponseDto> changeStoreOwner(@PathVariable("storeManagerId") Long storeManagerId) {
        storeService.changeStoreOwner(storeManagerId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store owner changed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping(value = "/manager/delete/{storeManagerId}")
    public ResponseEntity<ResponseDto> removeStoreManager(@PathVariable("storeManagerId") Long storeManagerId) {
        storeService.removeStoreManager(storeManagerId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store manager removed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
