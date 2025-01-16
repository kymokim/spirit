package com.kymokim.spirit.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security에 대한 디버깅 모드를 사용하기 위한 어노테이션 (default : false)
public class SecurityConfiguration{

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // REST API는 csrf 보안이 필요 없으므로 비활성화

                // JWT Token 인증방식으로 세션은 필요 없으므로 Stateless 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 인증 및 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/check-nickname").permitAll() // 로그인, 회원가입 허용
                        .requestMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**").permitAll() //스웨거 허용
                        .requestMatchers("**exception**").permitAll()
                )

                //나머지 요청은 인증된 USER 접근 가능
                .authorizeHttpRequests(authorize-> authorize.anyRequest().hasRole("USER"))

                //우리 서비스에 대한 권한은 있지만 다른 권한일 경우
                .exceptionHandling(handler -> handler.accessDeniedHandler(new CustomAccessDeniedHandler()))

                //우리 서비스에 권한 자체가 없을 경우
                .exceptionHandling(handler -> handler.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))

                // JWT 토큰 유효 체크 필터 추가 -> 아이디/비번 체크 필터 순으로 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}