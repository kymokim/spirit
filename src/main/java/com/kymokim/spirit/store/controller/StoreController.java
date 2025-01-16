package com.kymokim.spirit.store.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.dto.StoreSearchCriteria;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createStore(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                                   @RequestPart(value = "createStoreDto") RequestStore.CreateStoreDto createStoreDto) throws IOException {
        Long storeId = storeService.createStore(files, createStoreDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store created successfully.")
                .data(storeId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/uploadImg/{storeId}")
    public ResponseEntity<ResponseDto> uploadStoreImg(@RequestPart(value = "files", required = true) MultipartFile[] files,
                                                      @PathVariable("storeId") Long storeId) {
        storeService.uploadStoreImg(files, storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image uploaded successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/setMainImg/{storeId}")
    public ResponseEntity<ResponseDto> setMainImg(@RequestBody RequestStore.SetMainImgDto setMainImgDto){
        storeService.setMainImg(setMainImgDto.getUrl(), setMainImgDto.getStoreId());
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image set as main successfully.")
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

    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getAllStore() {
        List<ResponseStore.GetAllStoreDto> response = storeService.getAllStore();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{storeId}")
    public ResponseEntity<ResponseDto> getStore(@PathVariable("storeId") Long storeId,
                                                @RequestParam("latitude") double latitude,
                                                @RequestParam("longitude") double longitude) {
        StoreSearchCriteria criteria = new StoreSearchCriteria();
        criteria.setLatitude(latitude);
        criteria.setLongitude(longitude);
        criteria.setRadius(2);
        ResponseStore.GetStoreDto response = storeService.getStore(storeId, criteria);;
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Deprecated
    @PostMapping("/recommend")
    public ResponseEntity<ResponseDto> recommendStore(@RequestBody RequestStore.recommendStoreDto recommendStoreDto,
                                                      @RequestParam("latitude") double latitude,
                                                      @RequestParam("longitude") double longitude){
        StoreSearchCriteria criteria = new StoreSearchCriteria();
        criteria.setLatitude(latitude);
        criteria.setLongitude(longitude);
        criteria.setRadius(2);
        List<ResponseStore.GetAllStoreDto> response = storeService.recommendStore(criteria, recommendStoreDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/getByCategory/{category}")
    public ResponseEntity<ResponseDto> getByCategory(@PathVariable("category") String category,
                                                     @RequestParam("latitude") double latitude,
                                                     @RequestParam("longitude") double longitude) {
        StoreSearchCriteria criteria = new StoreSearchCriteria();
        criteria.setLatitude(latitude);
        criteria.setLongitude(longitude);
        criteria.setRadius(2);
        List<ResponseStore.GetAllStoreDto> response = storeService.getStoreByCategory(criteria, category);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/getByDistance")
    public ResponseEntity<ResponseDto> getByDistance(@RequestParam("latitude") double latitude,
                                                     @RequestParam("longitude") double longitude) {
        StoreSearchCriteria criteria = new StoreSearchCriteria();
        criteria.setLatitude(latitude);
        criteria.setLongitude(longitude);
        criteria.setRadius(2);
        List<ResponseStore.GetByDistanceDto> response = storeService.getStoreByDistance(criteria);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/getLikedStore")
    public ResponseEntity<ResponseDto> getLikedStore(){
        List<ResponseStore.GetLikedStoreDto> response = storeService.getLikedStore();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Liked store list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Deprecated
    @GetMapping("/getWrittenStore") //최근 방문 가게 리스트 구현하려다 자신이 작성한 가게 리스트 만듬...
    public ResponseEntity<ResponseDto> getWrittenStore() {
        List<ResponseStore.GetAllStoreDto> response = storeService.getByWriterStore();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/getRecentStore")
    public ResponseEntity<ResponseDto> getRecentStore() {
        List<ResponseStore.GetAllStoreDto> response = storeService.getByRecentVisitStore();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateStore(@RequestBody RequestStore.UpdateStoreDto updateStoreDto){
        storeService.updateStore(updateStoreDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<ResponseDto> deleteStore(@PathVariable("storeId") Long storeId){
        storeService.deleteStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
