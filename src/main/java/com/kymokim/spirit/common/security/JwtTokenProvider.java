package com.kymokim.spirit.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@PropertySource("classpath:/secret/jwt-secret-key.properties")
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    private final long accessTokenValidMillisecond = 1000L * 60 * 60; // 1시간
    private final long refreshTokenValidMillisecond = 1000L * 60 * 60 * 24 * 30; // 30일
    private final long millisecondBeforeExpiry = 1000L * 60 * 60 * 24 * 7; // 7일

    private Key signingKey;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long id) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidMillisecond))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long id) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);

            LOGGER.info("[validateToken] 토큰 유효 체크 완료");
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            LOGGER.info("[validateToken] 토큰 만료");
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.info("[validateToken] 토큰 유효 체크 예외 발생");
            throw e;
        }
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        LOGGER.info("[getAuthentication] 인증 정보 조회 완료, username: {}", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("x-auth-token");
    }

    public Boolean checkExpiry(String token) {
        Date now = new Date();
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
        return claims.getBody().getExpiration().before(new Date(now.getTime() + millisecondBeforeExpiry));
    }
}