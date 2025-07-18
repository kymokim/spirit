package com.kymokim.spirit.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreShareService {
    // 공유 링크 도메인
    @Value("${app.share.domain:https://dev.team-spirit.click}")
    private String shareDomain;
    // Android 패키지명 + Play 스토어 URL
    @Value("${app.android.package:com.spiritfront}")
    private String androidPackage;
    @Value("${app.store.fallback.android:https://play.google.com/store/apps/details?id=com.spiritfront}")
    private String androidStoreUrl;
    // iOS 앱스토어 URL
    @Value("${app.store.fallback.ios:https://apps.apple.com/kr/app/%ED%95%9C%EC%9E%94%ED%95%A0%EA%B9%8C/id6740095371}")
    private String iosStoreUrl;

    /**
     * 매장 공유 URL 생성 API에서 사용
     */
    public String buildShareLink(Long storeId) {
        return UriComponentsBuilder.fromUriString(shareDomain)
                .path("/ul/store/")
                .path(storeId.toString())
                .build()
                .toUriString();
    }

    /**
     * 공유 URL 클릭 시 OS에 맞춘 최종 리다이렉션 URL 반환.
     *  ▸ Android : intent:// + PlayStore fallback 포함
     *  ▸ iOS     : /ul/store/{id} (Universal Link)
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
            // iOS : Universal Link 경로로 넘김 (앱 미설치 → Safari 열림)
            return shareDomain + "/ul/store/" + storeId;
        }
        // 기타 : playStore link
        return URI.create(androidStoreUrl).toString();
    }

    /**
     * iOS Safari에서 앱을 열지 못했을 때 → App Store 이동 HTML 반환
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