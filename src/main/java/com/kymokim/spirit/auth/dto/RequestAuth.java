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
    public static class MergeUserDto{
        @NotEmpty
        private CommonAuth.SocialInfoDto originalSocialInfoDto;
        @NotEmpty
        private CommonAuth.SocialInfoDto newSocialInfoDto;
    }
}