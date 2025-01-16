package com.kymokim.spirit.auth.dto;

import com.kymokim.spirit.auth.entity.SocialInfo;
import com.kymokim.spirit.auth.entity.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public class CommonAuth {
    @Data
    @Builder
    public static class SocialInfoDto{
        @Schema(description = "소셜 로그인 종류")
        private SocialType type;
        @Schema(description = "소셜 로그인 ID")
        private String id;

        public static SocialInfoDto toDto(SocialInfo socialInfo){
            return SocialInfoDto.builder()
                    .type(socialInfo.getType())
                    .id(socialInfo.getId())
                    .build();
        }

        public static SocialInfo toEntity(SocialInfoDto socialInfoDto){
            return SocialInfo.builder()
                    .type(socialInfoDto.getType())
                    .id(socialInfoDto.getId())
                    .build();
        }
    }
}
