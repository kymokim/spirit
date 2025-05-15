package com.kymokim.spirit.store.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.service.StoreQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store Query API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store/get-by")
public class StoreQueryController {

    private final StoreQueryService storeQueryService;
    private final Logger LOGGER = LoggerFactory.getLogger(StoreQueryController.class);

    private LocationCriteria setCriteria(double latitude, double longitude, double radius){
        LocationCriteria criteria = new LocationCriteria();
        criteria.setLatitude(latitude);
        criteria.setLongitude(longitude);
        criteria.setRadius(radius);
        return criteria;
    }

    @Operation(summary = "가게 상세 조회")
    @GetMapping("/{storeId}")
    public ResponseEntity<ResponseDto> getStore(@PathVariable("storeId") Long storeId) {
        LOGGER.info("Store Query/getStore API called.");
        ResponseStore.GetStoreDto getStoreDto = storeQueryService.getStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store retrieved successfully.")
                .data(getStoreDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "가게 검색(가게명, 메뉴명) 리스트 조회")
    @GetMapping("/keyword/{keyword}")
    public ResponseEntity<ResponseDto> searchStore(@PathVariable("keyword") String keyword,
                                                   @RequestParam("latitude") double latitude,
                                                   @RequestParam("longitude") double longitude,
                                                   @RequestParam(value = "radius", defaultValue = "2") double radius,
                                                   @PageableDefault(size = 10) Pageable pageable){
        LOGGER.info("Store Query/searchStore API called.");
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        Page<ResponseStore.SearchStoreDto> dtoPage = storeQueryService.searchStore(criteria, keyword, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store search list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "전체 가게 검색(가게명) 리스트 조회")
    @GetMapping("/keyword/all/{keyword}")
    public ResponseEntity<ResponseDto> searchAllStore(@PathVariable("keyword") String keyword,
                                                      @PageableDefault(size = 10) Pageable pageable){
        LOGGER.info("Store Query/searchAllStore API called.");
        Page<ResponseStore.SearchAllStoreDto> dtoPage = storeQueryService.searchAllStore(keyword, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store all search list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "가까운 순서 가게 리스트 조회")
    @GetMapping("/distance")
    public ResponseEntity<ResponseDto> getByDistance(@RequestParam("latitude") double latitude,
                                                     @RequestParam("longitude") double longitude,
                                                     @RequestParam(value = "radius", defaultValue = "2") double radius,
                                                     @PageableDefault(size = 10) Pageable pageable){
        LOGGER.info("Store Query/getByDistance API called.");
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        Page<ResponseStore.GetByDistanceDto> dtoPage = storeQueryService.getByDistance(criteria, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store search list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "카테고리 가게 리스트 조회")
    @GetMapping("/category/{category}")
    public ResponseEntity<ResponseDto> getByCategory(@PathVariable("category") String category,
                                                     @RequestParam("latitude") double latitude,
                                                     @RequestParam("longitude") double longitude,
                                                     @RequestParam(value = "radius", defaultValue = "2") double radius,
                                                     @PageableDefault(size = 10) Pageable pageable) {
        LOGGER.info("Store Query/getByCategory API called.");
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        Page<ResponseStore.GetByCategoryDto> dtoPage = storeQueryService.getByCategory(criteria, category, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "영업중 가게 리스트 조회")
    @GetMapping("/business-hours")
    public ResponseEntity<ResponseDto> getByBusinessHours(@RequestParam("latitude") double latitude,
                                                          @RequestParam("longitude") double longitude,
                                                          @RequestParam(value = "radius", defaultValue = "2") double radius,
                                                          @PageableDefault(size = 10) Pageable pageable){
        LOGGER.info("Store Query/getByBusinessHours API called.");
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        Page<ResponseStore.GetByBusinessHoursDto> dtoPage = storeQueryService.getByBusinessHours(criteria, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store search list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "반경 내 가게 리스트 조회")
    @GetMapping("/radius")
    public ResponseEntity<ResponseDto> getByRadius(@RequestParam("latitude") double latitude,
                                                   @RequestParam("longitude") double longitude,
                                                   @RequestParam(value = "radius", defaultValue = "2") double radius){
        LOGGER.info("Store Query/getByRadius API called.");
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        List<ResponseStore.GetByRadiusDto> dtoList = storeQueryService.getByRadius(criteria);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store search list retrieved successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "좋아요한 가게 리스트 조회")
    @GetMapping("/liked")
    public ResponseEntity<ResponseDto> getLikedStore(@PageableDefault(size = 10, sort = "id") Pageable pageable){
        LOGGER.info("Store Query/getLikedStore API called.");
        Page<ResponseStore.GetLikedStoreDto> dtoPage = storeQueryService.getLikedStore(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Liked store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "최근 방문(리뷰 작성) 가게 조회")
    @GetMapping("/recent")
    public ResponseEntity<ResponseDto> getRecentStore(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        LOGGER.info("Store Query/getRecentStore API called.");
        Page<ResponseStore.GetRecentStoreDto> dtoPage = storeQueryService.getRecentStore(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "다중 조건 검색 리스트 조회")
    @GetMapping("/condition-search")
    public ResponseEntity<ResponseDto> conditionSearchStore(@RequestParam("latitude") double latitude,
                                                            @RequestParam("longitude") double longitude,
                                                            @RequestParam(value = "radius", defaultValue = "2") double radius,
                                                            @PageableDefault(size = 10) Pageable pageable,
                                                            @Valid RequestStore.ConditionSearchDto conditionSearchDto){
        LOGGER.info("Store Query/conditionSearchStore API called.");
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        Page<ResponseStore.SearchStoreDto> dtoPage = storeQueryService.conditionSearchStore(criteria, conditionSearchDto, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store condition search list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "메인 배너 리스트 조회")
    @GetMapping("/main-banner")
    public ResponseEntity<ResponseDto> getMainBanner(@RequestParam("latitude") double latitude,
                                                     @RequestParam("longitude") double longitude,
                                                     @RequestParam(value = "radius", defaultValue = "2") double radius){
        LOGGER.info("Store Query/getMainBanner API called.");
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        ResponseStore.GetMainBannerDto dto = storeQueryService.getMainBanner(criteria);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Main banner list retrieved successfully.")
                .data(dto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "매장 권한 등록 리스트 조회")
    @GetMapping("/ownership/list")
    public ResponseEntity<ResponseDto> getOwnershipList(@PageableDefault(size = 10)Pageable pageable) {
        LOGGER.info("Store/getOwnershipList API called.");
        Page<ResponseStore.OwnershipListDto> dtoPage = storeQueryService.getOwnershipList(pageable);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Store get ownership list successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "관리 중인 매장 리스트 조회")
    @GetMapping("/managed")
    public ResponseEntity<ResponseDto> getManagedStoreList(@PageableDefault(size = 10)Pageable pageable) {
        LOGGER.info("Store/getManagedStoreList API called.");
        Page<ResponseStore.ManagedStoreListDto> dtoPage = storeQueryService.getManagedStoreList(pageable);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Managed store list get successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "매장 권한 등록 조회")
    @GetMapping("/ownership/{ownershipId}")
    public ResponseEntity<ResponseDto> getOwnership(@PathVariable("ownershipId") Long ownershipId) {
        LOGGER.info("Store/getOwnership API called.");
        ResponseStore.OwnershipDto getOwnershipDto = storeQueryService.getOwnership(ownershipId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store get ownership list successfully.")
                .data(getOwnershipDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }



}
