package com.kymokim.spirit.store.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.dto.StoreSearchCriteria;
import com.kymokim.spirit.store.service.StoreQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store Query API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store/get")
public class StoreQueryController {

    private final StoreQueryService storeQueryService;

    @GetMapping("/{storeId}")
    public ResponseEntity<ResponseDto> getStore(@PathVariable("storeId") Long storeId) {
        ResponseStore.GetStoreDto getStoreDto = storeQueryService.getStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store retrieved successfully.")
                .data(getStoreDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDto> getAllStore(){
        List<ResponseStore.GetAllStoreDto> getAllStoreDtoList = storeQueryService.getAllStore();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(getAllStoreDtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

//    @GetMapping("/getByCategory/{category}")
//    public ResponseEntity<ResponseDto> getByCategory(@PathVariable("category") String category,
//                                                     @RequestParam("latitude") double latitude,
//                                                     @RequestParam("longitude") double longitude) {
//        StoreSearchCriteria criteria = new StoreSearchCriteria();
//        criteria.setLatitude(latitude);
//        criteria.setLongitude(longitude);
//        criteria.setRadius(2);
//        List<ResponseStore.GetAllStoreDto> response = storeQueryService.getStoreByCategory(criteria, category);
//        ResponseDto responseDto = ResponseDto.builder()
//                .message("Store list retrieved successfully.")
//                .data(response)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }
//
//    @GetMapping("/getByDistance")
//    public ResponseEntity<ResponseDto> getByDistance(@RequestParam("latitude") double latitude,
//                                                     @RequestParam("longitude") double longitude) {
//        StoreSearchCriteria criteria = new StoreSearchCriteria();
//        criteria.setLatitude(latitude);
//        criteria.setLongitude(longitude);
//        criteria.setRadius(2);
//        List<ResponseStore.GetByDistanceDto> response = storeQueryService.getStoreByDistance(criteria);
//        ResponseDto responseDto = ResponseDto.builder()
//                .message("Store list retrieved successfully.")
//                .data(response)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }
//
//    @GetMapping("/getLikedStore")
//    public ResponseEntity<ResponseDto> getLikedStore(){
//        List<ResponseStore.GetLikedStoreDto> response = storeQueryService.getLikedStore();
//        ResponseDto responseDto = ResponseDto.builder()
//                .message("Liked store list retrieved successfully.")
//                .data(response)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }
//
//    @GetMapping("/getRecentStore")
//    public ResponseEntity<ResponseDto> getRecentStore() {
//        List<ResponseStore.GetAllStoreDto> response = storeQueryService.getByRecentVisitStore();
//        ResponseDto responseDto = ResponseDto.builder()
//                .message("Store list retrieved successfully.")
//                .data(response)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }
}
