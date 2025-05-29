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
        private SocialType socialType;
        @Schema(description = "소셜 로그인 ID")
        private String socialId;

        public static SocialInfoDto toDto(SocialInfo socialInfo){
            return SocialInfoDto.builder()
                    .socialType(socialInfo.getSocialType())
                    .socialId(socialInfo.getSocialId())
                    .build();
        }

        public SocialInfo toEntity(){
            return SocialInfo.builder()
                    .socialType(this.socialType)
                    .socialId(this.socialId)
                    .build();
        }
    }
}
