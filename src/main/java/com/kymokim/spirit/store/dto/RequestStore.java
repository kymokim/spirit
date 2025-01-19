package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.MainDrink;
import com.kymokim.spirit.store.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;


public class RequestStore {

    @Data
    @Builder
    public static class CreateStoreDto {
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
        @Schema(description = "위치 정보")
        @NotEmpty(message = "위치 정보가 비었습니다.")
        private CommonStore.LocationDto locationDto;
        @Schema(description = "영업 시간")
        @NotEmpty(message = "영업 시간이 비었습니다.")
        private CommonStore.BusinessHoursDto businessHoursDto;
        @Schema(description = "카테고리")
        @NotEmpty(message = "카테고리가 비었습니다.")
        private Set<Category> categories;
        @Schema(description = "대표 주종")
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        @Schema(description = "휴무일")
        private Set<DayOfWeek> closedDays;

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
                    .businessHours(this.businessHoursDto.toEntity())
                    .categories(this.categories)
                    .mainDrinks(mainDrinks)
                    .closedDays(this.closedDays)
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
        @Schema(description = "위치 정보")
        private CommonStore.LocationDto locationDto;
        @Schema(description = "영업 시간")
        private CommonStore.BusinessHoursDto businessHoursDto;
        @Schema(description = "카테고리")
        private Set<Category> categories;
        @Schema(description = "대표 주종")
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        @Schema(description = "휴무일")
        private Set<DayOfWeek> closedDays;
    }
}
