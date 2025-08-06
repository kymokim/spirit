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
                        .requestMatchers("/api/auth/merge").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/actuator/prometheus").permitAll()
                        .requestMatchers("/api/version/check").permitAll()
                        .requestMatchers("/api/store/share/{storeId}", "/link/store/{storeId}", "/ul/store/{storeId}").permitAll()
                        .requestMatchers("/.well-known/**").permitAll()
                        .requestMatchers("/app-ads.txt").permitAll()
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
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("*");
        config.setAllowCredentials(false);

//        config.setAllowedOriginPatterns(List.of(
//                "https://teamspirit19.netlify.app",
//                "https://dev.team-spirit.click"
//        ));
        //        config.setAllowCredentials(true);

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}