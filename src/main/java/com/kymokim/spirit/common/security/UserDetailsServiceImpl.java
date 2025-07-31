package com.kymokim.spirit.common.security;

import com.kymokim.spirit.auth.auth.exception.AuthErrorCode;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.auth.auth.entity.Auth;
import com.kymokim.spirit.auth.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Auth auth = authRepository.findById(Long.valueOf(username))
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        return auth;
    }
}
