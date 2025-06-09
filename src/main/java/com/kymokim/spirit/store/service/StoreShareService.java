package com.kymokim.spirit.store.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

// ─────────────────────────────────────────────────────────────────────
// 1) Service : 공유 URL 생성 + 리다이렉션 URL 산출
// ─────────────────────────────────────────────────────────────────────
@Service
@Slf4j
@RequiredArgsConstructor
public class StoreShareService {

    // 공유 링크 도메인 (기본값 localhost)
    @Value("${app.share.domain:https://dev.team-spirit.click}")
    private String shareDomain;

    // Android 패키지명 + Play 스토어 URL (기본값 데모)
    @Value("${app.android.package:com.spiritfront}")
    private String androidPackage;
    @Value("${app.store.fallback.android:https://play.google.com/store/apps/details?id=com.spiritfront}")
    private String androidStoreUrl;

    // iOS 앱스토어 URL (기본값 데모)
    @Value("${app.store.fallback.ios:https://apps.apple.com/kr/app/%ED%95%9C%EC%9E%94%ED%95%A0%EA%B9%8C/id6740095371}")
    private String iosStoreUrl;

    /**
     * 매장 공유 URL 생성 API에서 사용.
     * ex) https://share.hanjan.app/link/store/205
     */
    public String buildShareLink(Long storeId) {
        return UriComponentsBuilder.fromHttpUrl(shareDomain)
                .path("/link/store/")
                .path(storeId.toString())
                .build()
                .toUriString();
    }

    /**
     * 공유 URL 클릭 시 OS에 맞춘 최종 리다이렉션 URL 반환.
     *  ▸ Android : intent:// + PlayStore fallback 포함
     *  ▸ iOS     : /ul/store/{id} (Universal Link)
     */
    public String getRedirectTarget(Long storeId, String userAgent) {
        String ua = userAgent != null ? userAgent.toLowerCase() : "";
        boolean isAndroid = ua.contains("android");
        boolean isIOS     = ua.contains("iphone") || ua.contains("ipad");

        if (isAndroid) {
            // intent://store/{id}#Intent;scheme=https;package=com.app;S.browser_fallback_url=play;end;
            String play = URI.create(androidStoreUrl).toString();
            return "intent://store/" + storeId +
                    "#Intent;scheme=https;package=" + androidPackage +
                    ";S.browser_fallback_url=" + play + ";end;";
        }
        if (isIOS) {
            // iOS / 기타 : Universal Link 경로로 넘김 (앱 미설치→Safari 열림)
            return shareDomain + "/ul/store/" + storeId;
        }
        return URI.create(androidStoreUrl).toString();
    }

    /**
     * iOS Safari에서 앱을 열지 못했을 때 → App Store 이동 HTML 반환
     */
    public String iosStoreHtml() {
        return """
                <html><head><meta charset='utf-8'><title>Redirect…</title></head>
                <body style='font-family:sans-serif;'>앱스토어로 이동 중입니다...<script>
                setTimeout(function(){window.location='%s';},300);
                </script></body></html>
                """.formatted(iosStoreUrl);
    }
}