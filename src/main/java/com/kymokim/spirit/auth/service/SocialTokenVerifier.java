package com.kymokim.spirit.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kymokim.spirit.auth.entity.SocialType;
import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialTokenVerifier {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String verify(SocialType type, String token) {
        try {
            switch (type) {
                case KAKAO:
                    return verifyKakaoToken(token);
                case GOOGLE:
                    return verifyGoogleToken(token);
                case APPLE:
                    return verifyAppleToken(token);
                default:
                    throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다: " + type);
            }
        } catch (Exception e) {
            log.error("[SocialTokenVerifier] 토큰 검증 실패", e);
            return null;
        }
    }

    // token : accessToken
    private String verifyKakaoToken(String token) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.get("id").asText();
        } else {
            throw new CustomException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }

    // token : idToken
    private String verifyGoogleToken(String token) throws Exception {
        String url = UriComponentsBuilder
                .newInstance()
                .uri(URI.create("https://oauth2.googleapis.com/tokeninfo"))
                .queryParam("id_token", token)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.get("sub").asText();
        } else {
            throw new CustomException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }

    // token : identityToken
    private String verifyAppleToken(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new CustomException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
        }
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        JsonNode payload = objectMapper.readTree(payloadJson);
        if (Objects.equals(payload.get("sub"), null)){
            throw new CustomException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
        }
        return payload.get("sub").asText();
    }
}
