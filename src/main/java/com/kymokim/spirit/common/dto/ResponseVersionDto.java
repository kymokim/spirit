package com.kymokim.spirit.common.dto;

import lombok.Builder;
import lombok.Getter;

public class ResponseVersionDto {

    @Builder
    @Getter
    public static class CheckVersionDto {
        private Boolean isUpdateRequired;
        private Boolean isTestEnabled;

        public static CheckVersionDto toDto(Boolean isUpdateRequired, Boolean isTestEnabled) {
            return CheckVersionDto.builder()
                    .isUpdateRequired(isUpdateRequired)
                    .isTestEnabled(isTestEnabled)
                    .build();
        }
    }
}
