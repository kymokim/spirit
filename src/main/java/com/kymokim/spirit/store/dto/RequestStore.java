package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Gender;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.store.entity.*;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
        @Schema(description = "스크린 보유 여부")
        @NotEmpty(message = "스크린 보유 여부가 비었습니다.")
        private Boolean hasScreen;
        @Schema(description = "단체석 보유 여부")
        @NotEmpty(message = "단체석 보유 여부가 비었습니다.")
        private Boolean isGroupAvailable;
        @Schema(description = "24시간 운영 여부")
        @NotEmpty(message = "24시간 운영 여부가 비었습니다.")
        private Boolean isAlwaysOpen;
        @Schema(description = "위치 정보")
        @NotEmpty(message = "위치 정보가 비었습니다.")
        private CommonStore.LocationDto locationDto;
        @Schema(description = "카테고리")
        @NotEmpty(message = "카테고리가 비었습니다.")
        private Set<Category> categories;
        @Schema(description = "대표 주종")
        @NotEmpty(message = "대표 주종이 비었습니다.")
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        @Schema(description = "운영 정보")
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;

        public Store toEntity(Long creatorId) {

            Set<MainDrink> mainDrinks = new HashSet<>();
            if (!this.mainDrinkDtos.isEmpty()) {
                this.mainDrinkDtos.forEach(mainDrinkDto -> mainDrinks.add(mainDrinkDto.toEntity()));
            }
            return Store.builder()
                    .name(this.name)
                    .contact(this.contact)
                    .description(this.description)
                    .hasScreen(this.hasScreen)
                    .isGroupAvailable(this.isGroupAvailable)
                    .creatorId(creatorId)
                    .location(this.locationDto.toEntity())
                    .categories(this.categories)
                    .mainDrinks(mainDrinks)
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
        @Schema(description = "스크린 보유 여부")
        private Boolean hasScreen;
        @Schema(description = "단체석 보유 여부")
        private Boolean isGroupAvailable;
        @Schema(description = "24시간 운영 여부")
        private Boolean isAlwaysOpen;
        @Schema(description = "위치 정보")
        @NotEmpty(message = "위치 정보가 비었습니다.")
        private CommonStore.LocationDto locationDto;
        @Schema(description = "카테고리")
        private Set<Category> categories;
        @Schema(description = "대표 주종")
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        @Schema(description = "운영 정보")
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;

        public Store toEntity(Long creatorId) {

            Set<MainDrink> mainDrinks = new HashSet<>();
            if (!Objects.equals(this.mainDrinkDtos, null) && !this.mainDrinkDtos.isEmpty()) {
                this.mainDrinkDtos.forEach(mainDrinkDto -> mainDrinks.add(mainDrinkDto.toEntity()));
            }
            return Store.fromSuggestion(this.name, this.contact, this.description, this.hasScreen,
                    this.isGroupAvailable, creatorId, this.locationDto.toEntity(), this.categories, mainDrinks);
        }
    }

    @Data
    @Builder
    public static class CreateStoreWithOwnershipRqDto {
        @NotEmpty
        private CreateStoreRqDto createStoreRqDto;
        @NotEmpty
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
        private List<RepresentativeInfo> representativeInfoList;
        @Schema(description = "주소")
        @NotEmpty(message = "주소가 비었습니다.")
        private CommonStore.LocationDto businessLocation;

        public OwnershipRequest toEntity(Store store, Auth requester) {
            return OwnershipRequest.builder()
                    .store(store)
                    .requester(requester)
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
        @Schema(description = "스크린 보유 여부")
        private Boolean hasScreen;
        @Schema(description = "단체석 보유 여부")
        private Boolean isGroupAvailable;
        @Schema(description = "24시간 운영 여부")
        private Boolean isAlwaysOpen;
        @Schema(description = "위치 정보")
        private CommonStore.LocationDto locationDto;
        @Schema(description = "카테고리")
        private Set<Category> categories;
        @Schema(description = "대표 주종")
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        @Schema(description = "운영 정보")
        private Set<CommonStore.OperationInfoDto> operationInfoDtos;
    }

    @Data
    @Builder
    public static class UpdateStoreImageSortOrderDto{
        @NotEmpty
        private Long storeId;
        @NotEmpty
        private List<String> storeImageUrlInOrderList;
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
        @NotEmpty
        private String category;
        @Schema(description = "단체 방문 여부")
        @NotNull
        private Boolean isGroupAvailable;
        @Schema(description = "방문 예정 시간", example = "2025-02-03T13:58:27.816")
        @NotNull
        private LocalDateTime conditionTime;
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
