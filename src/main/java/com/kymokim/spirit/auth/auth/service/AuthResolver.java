package com.kymokim.spirit.auth.auth.service;

import com.kymokim.spirit.auth.auth.entity.Auth;
import com.kymokim.spirit.auth.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.auth.repository.AuthRepository;
import com.kymokim.spirit.common.exception.CustomException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthResolver {

    private static AuthRepository authRepository;

    public AuthResolver(AuthRepository authRepository) {
        AuthResolver.authRepository = authRepository;
    }

    public static Auth resolveUser() {
        Long userId = Long.valueOf(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return authRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
    }

    public static Auth resolveUser(Long userId) {
        return authRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
    }

    public static Long resolveUserId() {
        return Long.valueOf(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
    }
}
