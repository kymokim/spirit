package com.kymokim.spirit.common.dto;

import lombok.Builder;
import lombok.Getter;

public class ResponseVersionDto {

    @Builder
    @Getter
    public static class CheckVersionDto {
        private Boolean isUpdateRequired;

        public static CheckVersionDto toDto(Boolean isUpdateRequired) {
            return CheckVersionDto.builder()
                    .isUpdateRequired(isUpdateRequired)
                    .build();
        }
    }
}
