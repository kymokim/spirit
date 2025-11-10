package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.auth.entity.Gender;
import com.kymokim.spirit.drink.entity.DrinkType;
import com.kymokim.spirit.store.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RequestStore {
    @Data
    @Builder
    public static class CreateStoreRqDto {
        @Schema(description = "이름")
        @NotEmpty(message = "이름이 비었습니다.")
        private String name;
        @Schema(description = "연락처")
        private String contact;
        @Schema(description = "설명")
        private String description;
        @Schema(description = "편의시설 정보")
        @NotNull(message = "편의시설 정보가 비었습니다.")
        @Valid
        private CommonStore.FacilitiesInfoDto facilitiesInfoDto;
        @Schema(description = "24시간 운영 여부")
        @NotNull(message = "24시간 운영 여부가 비었습니다.")
        private Boolean isAlwaysOpen;
        @Schema(description = "위치 정보")
        @NotNull(message = "위치 정보가 비었습니다.")
        @Valid
        private CommonStore.LocationDto locationDto;
        @Schema(description = "카테고리")
        @NotEmpty(message = "카테고리가 비었습니다.")
        private Set<Category> categories;
        @Schema(description = "대표 주종")
        @NotEmpty(message = "대표 주종이 비었습니다.")
        private Set<DrinkType> mainDrinkTypes;
        @Schema(description = "분위기")
        private Set<Mood> moods;
        @Schema(description = "운영 정보")
        @Valid
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;

        public Store toEntity(Long creatorId) {

            return Store.builder()
                    .name(this.name)
                    .contact(this.contact)
                    .description(this.description)
                    .facilitiesInfo(this.facilitiesInfoDto.toEntity())
                    .creatorId(creatorId)
                    .location(this.locationDto.toEntity())
                    .categories(this.categories)
                    .mainDrinks(new HashSet<>())
                    .moods(this.moods)
                    .build();
        }
    }

    @Data
    @Builder
    public static class SuggestStoreDto {
        @Schema(description = "이름")
        @NotEmpty(message = "이름이 비었습니다.")
        private String name;
        @Schema(description = "연락처")
        private String contact;
        @Schema(description = "설명")
        private String description;
        @Schema(description = "편의시설 정보")
        @NotNull(message = "편의시설 정보가 비었습니다.")
        @Valid
        private CommonStore.FacilitiesInfoDto facilitiesInfoDto;
        @Schema(description = "24시간 운영 여부")
        private Boolean isAlwaysOpen;
        @Schema(description = "위치 정보")
        @NotNull(message = "위치 정보가 비었습니다.")
        @Valid
        private CommonStore.LocationDto locationDto;
        @Schema(description = "카테고리")
        private Set<Category> categories;
        @Schema(description = "대표 주종")
        private Set<DrinkType> mainDrinkTypes;
        @Schema(description = "분위기")
        private Set<Mood> moods;
        @Schema(description = "운영 정보")
        @Valid
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;

        public Store toEntity(Long creatorId) {

            return Store.fromSuggestion(
                    this.name,
                    this.contact,
                    this.description,
                    this.facilitiesInfoDto.toEntity(),
                    creatorId,
                    this.locationDto.toEntity(),
                    this.categories,
                    new HashSet<>(),
                    this.moods
            );
        }
    }

    @Data
    @Builder
    public static class CreateStoreWithOwnershipRqDto {
        @NotNull
        @Valid
        private CreateStoreRqDto createStoreRqDto;
        @NotNull
        @Valid
        private CreateOwnershipRqDto createOwnershipRqDto;
    }

    @Data
    @Builder
    public static class CreateOwnershipRqDto {

        @Schema(description = "매장 id")
        private Long storeId;

        @Schema(description = "매장 이름")
        @NotEmpty(message = "매장 이름이 비었습니다.")
        private String receivedStoreName;
        @Schema(description = "매장 연락처")
        @NotEmpty(message = "매장 연락처가 비었습니다.")
        private String receivedStoreContact;
        @Schema(description = "신청자 연락처")
        @NotEmpty(message = "신청자 연락처가 비었습니다.")
        private String receivedUserContact;

        @Schema(description = "사업자등록번호")
        @NotEmpty(message = "사업자등록번호가 비었습니다.")
        private String businessRegistrationNumber;
        @Schema(description = "개업연월일")
        @NotEmpty(message = "개업일이 비었습니다.")
        private String openingDate;
        @Schema(description = "주류판매신고번호")
        @NotEmpty(message = "주류판매신고번호가 비었습니다.")
        private String liquorReportNumber;
        @Schema(description = "대표자")
        @NotEmpty(message = "대표자가 비었습니다.")
        @Valid
        private List<RepresentativeInfo> representativeInfoList;
        @Schema(description = "주소")
        @NotNull(message = "주소가 비었습니다.")
        @Valid
        private CommonStore.LocationDto businessLocation;

        public OwnershipRequest toEntity(Store store, Long requesterId) {
            return OwnershipRequest.builder()
                    .store(store)
                    .requesterId(requesterId)
                    .receivedStoreName(this.receivedStoreName)
                    .receivedStoreContact(this.receivedStoreContact)
                    .receivedUserContact(this.receivedUserContact)
                    .businessRegistrationNumber(this.businessRegistrationNumber)
                    .representativeInfoList(this.representativeInfoList)
                    .openingDate(this.openingDate)
                    .liquorReportNumber(this.liquorReportNumber)
                    .businessLocation(this.businessLocation.toEntity())
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateStoreDto {
        @Schema(description = "대표 이미지 URL")
        private String mainImgUrl;
        @Schema(description = "이름")
        private String name;
        @Schema(description = "연락처")
        private String contact;
        @Schema(description = "설명")
        private String description;
        @Schema(description = "편의시설 정보")
        @Valid
        private CommonStore.FacilitiesInfoDto facilitiesInfoDto;
        @Schema(description = "24시간 운영 여부")
        private Boolean isAlwaysOpen;
        @Schema(description = "위치 정보")
        @Valid
        private CommonStore.LocationDto locationDto;
        @Schema(description = "카테고리")
        private Set<Category> categories;
        @Schema(description = "대표 주종")
        private Set<DrinkType> mainDrinkTypes;
        @Schema(description = "분위기")
        private Set<Mood> moods;
        @Schema(description = "운영 정보")
        @Valid
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
    }

    @Data
    @Builder
    public static class UpdateStoreImageSortOrderDto{
        @NotNull
        private Long storeId;
        @NotEmpty
        private List<String> storeImageUrlInOrderList;
    }

    @Data
    @Builder
    public static class UpdateBoardImageSortOrderDto{
        @NotNull
        private Long storeId;
        @NotEmpty
        private List<String> boardImageUrlInOrderList;
    }

    @Data
    @Builder
    public static class DeleteImageDto {
        @NotEmpty
        private List<String> imgUrlList;
    }

    @Data
    @Builder
    public static class ConditionSearchDto {
        @Schema(description = "카테고리")
        private String category;
        @Schema(description = "검색 키워드(가게명/메뉴명)")
        private String searchKeyword;
        @Schema(description = "스크린 보유 여부")
        private Boolean hasScreen;
        @Schema(description = "룸 보유 여부")
        private Boolean hasRoom;
        @Schema(description = "야외 좌석 보유 여부")
        private Boolean hasOutdoor;
        @Schema(description = "단체석 보유 여부")
        private Boolean isGroupAvailable;
        @Schema(description = "주차 가능 여부")
        private Boolean isParkingAvailable;
        @Schema(description = "콜키지 가능 여부")
        private Boolean isCorkageAvailable;
        @Schema(description = "방문 예정 시간", example = "2025-02-03T13:58:27.816")
        private LocalDateTime conditionTime;
        @Schema(description = "대표 주종", example = "SOJU")
        private DrinkType drinkType;
        @Schema(description = "분위기", example = "QUIET, MODERN")
        private Set<Mood> moods;

        public FacilitiesCondition toFacilitiesCondition() {
            return FacilitiesCondition.builder()
                    .hasScreen(this.hasScreen)
                    .hasRoom(this.hasRoom)
                    .hasOutdoor(this.hasOutdoor)
                    .isGroupAvailable(this.isGroupAvailable)
                    .isParkingAvailable(this.isParkingAvailable)
                    .isCorkageAvailable(this.isCorkageAvailable)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class LikedStoreStatFilter {
        private Long storeId;
        private Gender gender;
        private List<String> ageGroups;
        private List<String> groupBy;
    }
}
