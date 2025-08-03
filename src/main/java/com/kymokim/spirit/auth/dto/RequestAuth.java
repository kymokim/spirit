package com.kymokim.spirit.auth.dto;

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