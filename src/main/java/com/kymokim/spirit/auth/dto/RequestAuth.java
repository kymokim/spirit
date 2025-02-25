package com.kymokim.spirit.auth.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.entity.Gender;
import com.kymokim.spirit.auth.entity.PersonalInfo;
import com.kymokim.spirit.common.service.AESUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
public class RequestAuth {
    @Builder
    @Data
    public static class RegisterUserDto{
        @Schema(description = "소셜 로그인 정보")
        @NotEmpty(message = "소셜 로그인 정보가 비었습니다.")
        private CommonAuth.SocialInfoDto socialInfoDto;
        @Schema(description = "닉네임")
        @NotEmpty(message = "닉네임이 비었습니다.")
        private String nickname;

        public Auth toEntity(PersonalInfo personalInfo){
            return Auth.builder()
                    .socialInfo(this.socialInfoDto.toEntity())
                    .personalInfo(personalInfo)
                    .nickname(this.nickname)
                    .build();
        }
    }

    @Builder
    @Data
    public static class LoginUserRqDto{
        @Schema(description = "소셜 로그인 정보")
        @NotEmpty(message = "소셜 로그인 정보가 비었습니다.")
        private CommonAuth.SocialInfoDto socialInfoDto;
        @Schema(description = "소셜 로그인 토큰")
        @NotEmpty(message = "소셜 로그인 토큰이 비었습니다.")
        private String socialToken;
    }

    // 현재는 본인인증 api가 없으니 초기값 직접 초기화
    // 추후 본인인증 api 붙으면 그 쪽 서버에서 받아온 값을 매칭시키는 식으로 변경(이 때는 초기화 제거)
    @Data
    public static class PersonalInfoRqDto{
        @NotEmpty
        private String ci = "OTMyNjI3NzM0NDU0MzE0NjY1NjQwNjAwNzYwNzAwNzQwNzEwNzQxNzA3NTMwNzQxNzE0";
        @NotEmpty
        private String name = "홍길동";
        @NotEmpty
        private String birth_date = "19900101";
        @NotEmpty
        private String gender = "1";
        @NotEmpty
        private String phone_number = "+821012345678";

        public PersonalInfo toEntity(AESUtil aesUtil) {
            return PersonalInfo.builder()
                    .ci(aesUtil.encrypt(this.ci))
                    .name(aesUtil.encrypt(this.name))
                    .birthDate(aesUtil.encrypt(this.birth_date))
                    .gender(Gender.fromCode(this.gender))
                    .phoneNumber(aesUtil.encrypt(this.phone_number))
                    .build();
        }
    }
}