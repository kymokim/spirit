package com.kymokim.spirit.store.controller;

import com.kymokim.spirit.auth.entity.Gender;
import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.store.dto.LocationCriteria;
import com.kymokim.spirit.store.dto.RequestStore;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.service.StoreQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Objects;

@Tag(name = "Store Query API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store/get-by")
public class StoreQueryController {

    private final StoreQueryService storeQueryService;

    private LocationCriteria setCriteria(double latitude, double longitude, double radius) {
        System.out.println("radius: " + radius + "km");
        return LocationCriteria.builder()
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius)
                .build();
    }

    @Operation(summary = "가게 상세 조회")
    @GetMapping("/{storeId}")
    public ResponseEntity<ResponseDto> getStore(@PathVariable("storeId") Long storeId) {
        ResponseStore.GetStoreDto getStoreDto = storeQueryService.getStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store retrieved successfully.")
                .data(getStoreDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "가게 프리뷰 조회")
    @GetMapping("/preview/{storeId}")
    public ResponseEntity<ResponseDto> getStorePreview(@PathVariable("storeId") Long storeId) {
        ResponseStore.GetStorePreviewDto getStorePreviewDto = storeQueryService.getStorePreview(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store preview retrieved successfully.")
                .data(getStorePreviewDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "가게 검색(가게명, 메뉴명) 리스트 조회")
    @GetMapping("/keyword/{keyword}")
    public ResponseEntity<ResponseDto> searchStore(@PathVariable("keyword") String keyword,
                                                   @RequestParam("latitude") double latitude,
                                                   @RequestParam("longitude") double longitude,
                                                   @RequestParam("radius") double radius,
                                                   @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
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
                                                      @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        Page<ResponseStore.SearchAllStoreDto> dtoPage = storeQueryService.searchAllStore(keyword, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store all search list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "현재 운영자 조회")
    @GetMapping("/managers/{storeId}")
    public ResponseEntity<ResponseDto> getStoreManagers(@PathVariable("storeId") Long storeId) {
        List<ResponseStore.StoreManagerListDto> list = storeQueryService.getStoreManagers(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store manager list retrieved successfully.")
                .data(list)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "운영자 초대 프리뷰 조회")
    @GetMapping("/manager-invitation/preview/{managerInvitationId}")
    public ResponseEntity<ResponseDto> getManagerInvitationPreview(@PathVariable("managerInvitationId") String managerInvitationId) {
        ResponseStore.ManagerInvitationPreviewDto dto = storeQueryService.getManagerInvitationPreview(managerInvitationId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Manager invitation preview retrieved successfully.")
                .data(dto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "가까운 순서 가게 리스트 조회")
    @GetMapping("/distance")
    public ResponseEntity<ResponseDto> getByDistance(@RequestParam("latitude") double latitude,
                                                     @RequestParam("longitude") double longitude,
                                                     @RequestParam("radius") double radius,
                                                     @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
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
                                                     @RequestParam("radius") double radius,
                                                     @RequestParam(value = "drinkType", required = false) DrinkType drinkType,
                                                     @RequestParam(value = "priceOrder", required = false) Sort.Direction priceOrder,
                                                     @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        Page<ResponseStore.GetByCategoryDto> dtoPage = storeQueryService.getByCategory(criteria, category, drinkType, priceOrder, pageable);
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
                                                          @RequestParam("radius") double radius,
                                                          @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        Page<ResponseStore.GetByBusinessHoursDto> dtoPage = storeQueryService.getByBusinessHours(criteria, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store search list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "좋아요한 가게 리스트 조회")
    @GetMapping("/liked")
    public ResponseEntity<ResponseDto> getLikedStore(@ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<ResponseStore.GetLikedStoreDto> dtoPage = storeQueryService.getLikedStore(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Liked store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Deprecated
    @Operation(summary = "반경 내 가게 리스트 조회")
    @GetMapping("/radius")
    public ResponseEntity<ResponseDto> getByRadius(@RequestParam("latitude") double latitude,
                                                   @RequestParam("longitude") double longitude,
                                                   @RequestParam("radius") double radius) {
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        List<ResponseStore.GetByRadiusDto> dtoList = storeQueryService.getByRadius(criteria);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store search list retrieved successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "다중 조건 검색 리스트 조회")
    @GetMapping("/condition-search")
    public ResponseEntity<ResponseDto> conditionSearchStore(@RequestParam("latitude") double latitude,
                                                            @RequestParam("longitude") double longitude,
                                                            @RequestParam("radius") double radius,
                                                            @ParameterObject @PageableDefault(size = 10) Pageable pageable,
                                                            @Valid RequestStore.ConditionSearchDto conditionSearchDto) {
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        Page<ResponseStore.SearchStoreDto> dtoPage = storeQueryService.conditionSearchStore(criteria, conditionSearchDto, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store condition search list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "다중 조건 검색 마커 리스트 조회")
    @GetMapping("/condition-search/markers")
    public ResponseEntity<ResponseDto> conditionSearchStoreMarkers(@RequestParam("latitude") double latitude,
                                                                   @RequestParam("longitude") double longitude,
                                                                   @RequestParam("radius") double radius,
                                                                   @Valid RequestStore.ConditionSearchDto conditionSearchDto) {
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        List<ResponseStore.MapMarkerDto> dtoList = storeQueryService.conditionSearchStoreMarkers(criteria, conditionSearchDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store condition search markers retrieved successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "메인 배너 리스트 조회")
    @GetMapping("/main-banner")
    public ResponseEntity<ResponseDto> getMainBanner(@RequestParam("latitude") double latitude,
                                                     @RequestParam("longitude") double longitude,
                                                     @RequestParam("radius") double radius) {
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        ResponseStore.GetMainBannerDto dto = storeQueryService.getMainBanner(criteria);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Main banner list retrieved successfully.")
                .data(dto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "매장 제보 리스트 조회")
    @GetMapping("/suggestion/list")
    public ResponseEntity<ResponseDto> getStoreSuggestionList(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        Page<ResponseStore.StoreSuggestionListDto> dtoPage = storeQueryService.getStoreSuggestionList(pageable);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Store suggestion list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "매장 권한 등록 리스트 조회")
    @GetMapping("/ownership/list")
    public ResponseEntity<ResponseDto> getOwnershipList(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        Page<ResponseStore.OwnershipListDto> dtoPage = storeQueryService.getOwnershipList(pageable);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Store get ownership list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "매장 제보 & 권한 등록 동시 진행 리스트 조회")
    @GetMapping("/ownership/with-suggestion")
    public ResponseEntity<ResponseDto> getOwnershipListWithStoreSuggestion(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        Page<ResponseStore.GetOwnershipListWithStoreSuggestionDto> dtoPage = storeQueryService.getOwnershipListWithStoreSuggestion(pageable);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Store get ownership with suggestion list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "관리 중인 매장 리스트 조회")
    @GetMapping("/managed")
    public ResponseEntity<ResponseDto> getManagedStoreList(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        Page<ResponseStore.ManagedStoreListDto> dtoPage = storeQueryService.getManagedStoreList(pageable);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Managed store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "매장 권한 등록 조회")
    @GetMapping("/ownership/{ownershipId}")
    public ResponseEntity<ResponseDto> getOwnership(@ParameterObject @PathVariable("ownershipId") Long ownershipId) {
        ResponseStore.OwnershipDto getOwnershipDto = storeQueryService.getOwnership(ownershipId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store get ownership list retrieved successfully.")
                .data(getOwnershipDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @Operation(summary = "가게 좋아요 통계")
    @GetMapping("/liked/stats")
    public ResponseEntity<ResponseDto> getRegisterLogStats(
            @RequestParam Long storeId,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) List<String> ageGroup,
            @RequestParam List<String> groupBy
    ) {
        RequestStore.LikedStoreStatFilter filter = new RequestStore.LikedStoreStatFilter(storeId, gender, ageGroup, groupBy);
        List<ResponseStore.LikedStoreStatDto> dtoList = storeQueryService.getLikedStoreStats(filter);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Liked store log stats retrieved successfully.")
                .data(dtoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "최근 조회한 매장 리스트 조회")
    @GetMapping("/viewed")
    public ResponseEntity<ResponseDto> getViewedStore(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        Page<ResponseStore.GetViewedStoreDto> dtoPage = storeQueryService.getViewedStore(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Viewed store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "인기 매장 리스트 조회")
    @GetMapping("/popular")
    public ResponseEntity<ResponseDto> getPopularStore(@RequestParam("latitude") double latitude,
                                                       @RequestParam("longitude") double longitude,
                                                       @RequestParam("radius") double radius,
                                                       @RequestParam(value = "drinkType", required = false) DrinkType drinkType,
                                                       @RequestParam(value = "priceOrder", required = false) Sort.Direction priceOrder,
                                                       @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        Page<ResponseStore.GetPopularStoreDto> dtoPage = storeQueryService.getPopularStore(criteria, drinkType, priceOrder, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Popular store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "주종별 최저가 매장 리스트 조회")
    @GetMapping("/drink-price")
    public ResponseEntity<ResponseDto> getByDrinkPrice(@RequestParam("latitude") double latitude,
                                                       @RequestParam("longitude") double longitude,
                                                       @RequestParam("radius") double radius,
                                                       @RequestParam("drinkType") DrinkType drinkType,
                                                       @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        LocationCriteria criteria = setCriteria(latitude, longitude, radius);
        Page<ResponseStore.GetByDrinkPriceDto> dtoPage = storeQueryService.getByDrinkPrice(criteria, drinkType, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Store list retrieved successfully.")
                .data(dtoPage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
