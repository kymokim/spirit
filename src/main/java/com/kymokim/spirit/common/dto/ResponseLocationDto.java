package com.kymokim.spirit.common.dto;

import lombok.Builder;
import lombok.Getter;

public class ResponseLocationDto {
    @Builder
    @Getter
    public static class GetAddressDto {
        private String address;

        public static GetAddressDto toDto(String address) {
            return GetAddressDto.builder()
                    .address(address)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetCoordinateDto {
        private Double latitude;
        private Double longitude;

        public static GetCoordinateDto toDto(Double latitude, Double longitude) {
            return GetCoordinateDto.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetRoadAddressAndCoordinateDto {
        private GetAddressDto getAddressDto;
        private GetCoordinateDto getCoordinateDto;

        public static GetRoadAddressAndCoordinateDto toDto(GetAddressDto getAddressDto, GetCoordinateDto getCoordinateDto) {
            return GetRoadAddressAndCoordinateDto.builder()
                    .getAddressDto(getAddressDto)
                    .getCoordinateDto(getCoordinateDto)
                    .build();
        }
    }
}
