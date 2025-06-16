package com.kymokim.spirit.common.dto;

import lombok.Builder;
import lombok.Getter;

public class ResponseVersionDto {

    @Builder
    @Getter
    public static class CheckVersionDto {
        private Boolean isVersionMatched;
        private Boolean isUpdateRequired;

        public static CheckVersionDto toDto(Boolean isVersionMatched, Boolean isUpdateRequired) {
            return CheckVersionDto.builder()
                    .isVersionMatched(isVersionMatched)
                    .isUpdateRequired(isUpdateRequired)
                    .build();
        }
    }
}
