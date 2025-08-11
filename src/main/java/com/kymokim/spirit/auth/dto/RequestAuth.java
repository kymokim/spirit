package com.kymokim.spirit.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RequiredArgsConstructor
public class RequestAuth {
    @Builder
    @Data
    public static class MergeUserDto{
        @NotNull
        @Valid
        private CommonAuth.SocialInfoDto originalSocialInfoDto;
        @NotNull
        @Valid
        private CommonAuth.SocialInfoDto newSocialInfoDto;
    }
}