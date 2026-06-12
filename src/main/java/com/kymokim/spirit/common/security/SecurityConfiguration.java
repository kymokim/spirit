package com.kymokim.spirit.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // Spring Security에 대한 디버깅 모드를 사용하기 위한 어노테이션 (default : false)
@EnableMethodSecurity // @PreAuthorize, @PostAuthorize, @Secured 활성화
public class SecurityConfiguration{

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${swagger.basic.username:}")
    private String swaggerBasicUsername;
    @Value("${swagger.basic.password:}")
    private String swaggerBasicPassword;

    @Autowired
    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // swagger.basic 자격증명이 설정된 환경(dev)에서만 Basic 인증 요구, 그 외(local)는 그대로 개방
        if (!isSwaggerBasicAuthConfigured()) {
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
            return http.build();
        }

        http
                .authenticationProvider(swaggerAuthenticationProvider())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("SWAGGER"))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    @Order(2)
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
                        .requestMatchers("/actuator/prometheus").permitAll()
                        .requestMatchers("/api/auth/clean-up").permitAll()
                        .requestMatchers("/api/version/check").permitAll()
                        .requestMatchers("/link/**", "/ul/**").permitAll()
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

    // swagger 전용 인증 프로바이더. UserDetailsService/PasswordEncoder를 별도 빈으로 노출하면
    // 기존 UserDetailsServiceImpl과 충돌(NoUniqueBeanDefinitionException)하므로 내부에서 인라인 구성한다.
    private AuthenticationProvider swaggerAuthenticationProvider() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        if (isSwaggerBasicAuthConfigured()) {
            manager.createUser(User.builder()
                    .username(swaggerBasicUsername)
                    .password(encoder.encode(swaggerBasicPassword))
                    .roles("SWAGGER")
                    .build());
        }
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(manager);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    private boolean isSwaggerBasicAuthConfigured() {
        return swaggerBasicUsername != null && !swaggerBasicUsername.isBlank()
                && swaggerBasicPassword != null && !swaggerBasicPassword.isBlank();
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
