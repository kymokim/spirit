package com.kymokim.spirit.auth.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Role;
import com.kymokim.spirit.auth.entity.SocialInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class ResponseAuth {
    @Data
    @Builder
    public static class LoginUserRsDto{
        @Schema(description = "액세스 토큰")
        private String accessToken;
        @Schema(description = "리프레쉬 토큰")
        private String refreshToken;

        public static LoginUserRsDto toDto(String accessToken, String refreshToken){
            return LoginUserRsDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    @Data
    @Builder
    public static class ReissueTokenDto{
        @Schema(description = "액세스 토큰")
        private String accessToken;
        @Schema(description = "리프레쉬 토큰")
        private String refreshToken;

        public static ReissueTokenDto toDto(String accessToken, String refreshToken){
            return ReissueTokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    @Data
    @Builder
    public static class GetUserDto{
        @Schema(description = "유저 id")
        private Long id;
        @Schema(description = "소셜 로그인 정보")
        private CommonAuth.SocialInfoDto socialInfoDto;
        @Schema(description = "회원가입 일시")
        private LocalDateTime createdAt;
        @Schema(description = "닉네임")
        private String nickname;
        @Schema(description = "이미지")
        private String imgUrl;
        @Schema(description = "사장 여부")
        private Boolean isManager;

        public static GetUserDto toDto(Auth user){
            return GetUserDto.builder()
                    .id(user.getId())
                    .socialInfoDto(CommonAuth.SocialInfoDto.toDto(user.getSocialInfo()))
                    .createdAt(user.getCreatedAt())
                    .nickname(user.getNickname())
                    .imgUrl(user.getImgUrl())
                    .isManager(user.getRoles().contains(Role.MANAGER))
                    .build();
        }
    }

    @Data
    @Builder
    public static class CheckNicknameDto{
        @Schema(description = "닉네임 사용 가능 여부")
        private Boolean isAvailable;

        public static CheckNicknameDto toDto(Boolean isAvailable){
            return CheckNicknameDto.builder()
                    .isAvailable(isAvailable)
                    .build();
        }
    }
}
