package com.kymokim.spirit.main.store.dto;

import com.kymokim.spirit.main.drink.entity.DrinkType;
import com.kymokim.spirit.main.store.entity.Location;
import com.kymokim.spirit.main.store.entity.MainDrink;
import com.kymokim.spirit.main.store.entity.OperationInfo;
import com.kymokim.spirit.main.store.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class CommonStore {
    @Data
    @Builder
    public static class LocationDto{
        @Schema(description = "주소")
        private String address;
        @Schema(description = "상세 주소")
        private String addressDetail;
        @Schema(description = "위도", example = "12.34")
        private double latitude;
        @Schema(description = "경도", example = "12.34")
        private double longitude;

        public static LocationDto toDto(Location location){
            return LocationDto.builder()
                    .address(location.getAddress())
                    .addressDetail(location.getAddressDetail())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
        }

        public Location toEntity(){
            return Location.builder()
                    .address(this.address)
                    .addressDetail(this.addressDetail)
                    .latitude(this.latitude)
                    .longitude(this.longitude)
                    .build();
        }
    }

    @Data
    @Builder
    public static class OperationInfoDto{
        @Schema(description = "요일")
        private DayOfWeek dayOfWeek;
        @Schema(description = "휴무 여부")
        private Boolean isClosed;
        @Schema(description = "영업 시작 시간", example = "13:00")
        private LocalTime openTime;
        @Schema(description = "영업 종료 시간", example = "21:00")
        private LocalTime closeTime;
        @Schema(description = "브레이크 시작 시간", example = "15:00")
        private LocalTime breakStartTime;
        @Schema(description = "브레이크 종료 시간", example = "17:00")
        private LocalTime breakEndTime;

        public static OperationInfoDto toDto(OperationInfo operationInfo){
            return OperationInfoDto.builder()
                    .dayOfWeek(operationInfo.getDayOfWeek())
                    .isClosed(operationInfo.getIsClosed())
                    .openTime(operationInfo.getOpenTime())
                    .closeTime(operationInfo.getCloseTime())
                    .breakStartTime(operationInfo.getBreakStartTime())
                    .breakEndTime(operationInfo.getBreakEndTime())
                    .build();
        }

        public OperationInfo toEntity(Store store){
            return OperationInfo.builder()
                    .dayOfWeek(this.dayOfWeek)
                    .isClosed(this.isClosed)
                    .openTime(this.openTime)
                    .closeTime(this.closeTime)
                    .breakStartTime(this.breakStartTime)
                    .breakEndTime(this.breakEndTime)
                    .store(store)
                    .build();
        }
    }

    @Data
    @Builder
    public static class MainDrinkDto{
        @Schema(description = "주종")
        private DrinkType type;
        @Schema(description = "가격")
        private Long price;

        public static MainDrinkDto toDto(MainDrink mainDrink){
            return MainDrinkDto.builder()
                    .type(mainDrink.getType())
                    .price(mainDrink.getPrice())
                    .build();
        }

        public MainDrink toEntity(){
            return MainDrink.builder()
                    .type(this.type)
                    .price(this.price)
                    .build();
        }
    }
}
