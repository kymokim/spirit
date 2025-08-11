package com.kymokim.spirit.common.security;

import com.kymokim.spirit.common.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Throwable cause = (Throwable) request.getAttribute("jakarta.servlet.error.exception");

        if (cause instanceof CustomException) {
            CustomException customException = (CustomException) cause;
            response.setStatus(customException.getErrorCode().getHttpStatus().value());
            response.getWriter().write(customException.getErrorCode().getMessage() + customException.getErrorCode().getCode());
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }
}