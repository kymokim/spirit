package com.example.Fooding.common.config;

import com.example.Fooding.auth.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        List<String> excludePatterns = Arrays.asList("/api/auth/register","/api/auth/login","/dev","/h2/**","/ws/**","/api/chat/**");//interceptor 제외할 부분
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePatterns);

    }
}
