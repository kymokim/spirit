package com.kymokim.spirit.auth.interceptor;

import com.kymokim.spirit.auth.security.JwtAuthToken;
import com.kymokim.spirit.auth.security.JwtAuthTokenProvider;
import com.kymokim.spirit.common.exception.error.CustomJwtRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        if(token.isPresent()){
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            if(jwtAuthToken.validate())
                return true;
            throw new CustomJwtRuntimeException();
        }
        throw new CustomJwtRuntimeException();
    }
}
