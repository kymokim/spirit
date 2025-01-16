package com.kymokim.spirit.auth.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.SocialInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RequestAuth {
    @Builder
    @Data
    public static class RegisterUserDto{
        @Schema(description = "소셜 로그인 정보")
        @NotEmpty(message = "소셜 로그인 정보가 비었습니다")
        private CommonAuth.SocialInfoDto socialInfoDto;
        @Schema(description = "닉네임")
        @NotEmpty(message = "닉네임이 비었습니다")
        private String nickname;

        public static Auth toEntity(RegisterUserDto registerUserDto){
            return Auth.builder()
                    .socialInfo(CommonAuth.SocialInfoDto.toEntity(registerUserDto.getSocialInfoDto()))
                    .nickname(registerUserDto.getNickname())
                    .build();
        }
    }

    @Builder
    @Data
    public static class LoginUserRqDto{
        @Schema(description = "소셜 로그인 정보")
        @NotEmpty(message = "소셜 로그인 정보가 비었습니다")
        private CommonAuth.SocialInfoDto socialInfoDto;
        @Schema(description = "소셜 로그인 토큰")
        @NotEmpty(message = "소셜 로그인 토큰이 비었습니다")
        private String socialToken;
    }
}