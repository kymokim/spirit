package com.kymokim.spirit.store.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.service.StoreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Store API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createStore(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                                   @RequestPart(value = "createStoreDto") RequestStore.CreateStoreDto createStoreDto) throws IOException {
        storeService.createStore(files, createStoreDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PatchMapping("/update/{storeId}")
    public ResponseEntity<ResponseDto> updateStore(@PathVariable Long storeId, @RequestBody RequestStore.UpdateStoreDto updateStoreDto) {
        storeService.updateStore(storeId, updateStoreDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/uploadImg/{storeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> uploadStoreImg(@RequestPart(value = "files", required = true) MultipartFile[] files,
                                                      @PathVariable("storeId") Long storeId) {
        storeService.uploadStoreImg(files, storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image uploaded successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/like/{storeId}")
    public ResponseEntity<ResponseDto> likeStore(@PathVariable("storeId") Long storeId){
        storeService.likeStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store liked successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/unlike/{storeId}")
    public ResponseEntity<ResponseDto> unlikeStore(@PathVariable("storeId") Long storeId){
        storeService.unlikeStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store unliked successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<ResponseDto> deleteStore(@PathVariable("storeId") Long storeId){
        storeService.deleteStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
