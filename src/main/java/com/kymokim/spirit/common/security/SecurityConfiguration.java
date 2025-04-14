package com.kymokim.spirit.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // Spring Security에 대한 디버깅 모드를 사용하기 위한 어노테이션 (default : false)
@EnableMethodSecurity // @PreAuthorize, @PostAuthorize, @Secured 활성화
public class SecurityConfiguration{

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // cors 설정 추가
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(AbstractHttpConfigurer::disable) // REST API는 csrf 보안이 필요 없으므로 비활성화

                // JWT Token 인증방식으로 세션은 필요 없으므로 Stateless 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 인증 및 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/check-nickname").permitAll() // 로그인, 회원가입 허용
                        .requestMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**").permitAll() //스웨거 허용
                        .requestMatchers("**exception**").permitAll()
                )

                //나머지 요청은 인증된 USER 접근 가능
                .authorizeHttpRequests(authorize-> authorize.anyRequest().hasRole("USER"))

                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 인증 실패
                        .accessDeniedHandler(new CustomAccessDeniedHandler())) // 접근 거부

                // JWT 토큰 유효 체크 필터 추가 -> 아이디/비번 체크 필터 순으로 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 관리자 페이지 호스팅 전 개발 단계에서만 전체 origin 허용, 추후 비활성화
        configuration.setAllowedOriginPatterns(List.of("*"));
        // 관리자 페이지 호스팅 시, 해당 도메인 추가 후 활성화
//        configuration.setAllowedOriginPatterns(List.of(
//                "https://admin.domain.com",
//        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        // Authorization 헤더 사용 또는 쿠키/세션 기반 인증인 경우 활성화
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}