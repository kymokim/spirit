package com.kymokim.spirit.store.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

import java.io.IOException;
import java.util.Arrays;
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
                                                   @RequestPart(value = "createStoreDto") RequestStore.CreateStoreRqDto createStoreRqDto) throws IOException {
        System.out.println("Store/createStore API called.");
        ResponseStore.CreateStoreRsDto createStoreRsDto = storeService.createStore(files, createStoreRqDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store created successfully.")
                .data(createStoreRsDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PatchMapping("/update/{storeId}")
    public ResponseEntity<ResponseDto> updateStore(@PathVariable Long storeId, @RequestBody RequestStore.UpdateStoreDto updateStoreDto) {
        System.out.println("Store/updateStore API called.");
        storeService.updateStore(storeId, updateStoreDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/upload-image/{storeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> uploadImage(@RequestPart(value = "files", required = true) MultipartFile[] files,
                                                   @PathVariable("storeId") Long storeId) {
        System.out.println("Store/uploadImage API called.");
        storeService.uploadImage(files, storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image uploaded successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/like/{storeId}")
    public ResponseEntity<ResponseDto> likeStore(@PathVariable("storeId") Long storeId){
        System.out.println("Store/likeStore API called.");
        storeService.likeStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store like processed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete-image/{storeId}")
    public ResponseEntity<ResponseDto> deleteImage(@RequestBody RequestStore.DeleteImageDto deleteImageDto,
                                                   @PathVariable("storeId") Long storeId){
        System.out.println("Store/deleteImage API called.");
        storeService.deleteImage(deleteImageDto, storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<ResponseDto> deleteStore(@PathVariable("storeId") Long storeId){
        System.out.println("Store/deleteStore API called.");
        storeService.deleteStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
