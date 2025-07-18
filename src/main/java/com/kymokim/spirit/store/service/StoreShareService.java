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

    @Value("${app.share.domain:https://dev.team-spirit.click}")
    private String shareDomain;

    @Value("${app.android.package:com.spiritfront}")
    private String androidPackage;

    @Value("${app.store.fallback.android:https://play.google.com/store/apps/details?id=com.spiritfront}")
    private String androidStoreUrl;

    @Value("${app.store.fallback.ios:https://apps.apple.com/kr/app/%ED%95%9C%EC%9E%94%ED%95%A0%EA%B9%8C/id6740095371}")
    private String iosStoreUrl;

    /**
     * 공유 링크 생성
     */
    public String buildShareLink(Long storeId) {
        return UriComponentsBuilder.fromUriString(shareDomain)
                .path("/ul/store/")
                .path(storeId.toString())
                .build()
                .toUriString();
    }

    /**
     * 기본 리다이렉션 URL
     */
    public String getRedirectTarget(Long storeId, String userAgent) {
        String ua = userAgent != null ? userAgent.toLowerCase() : "";
        boolean isAndroid = ua.contains("android");
        boolean isIOS = ua.contains("iphone") || ua.contains("ipad");

        if (isAndroid) {
            return "intent://store/" + storeId +
                    "#Intent;scheme=https;package=" + androidPackage +
                    ";S.browser_fallback_url=" + androidStoreUrl + ";end;";
        }

        if (isIOS) {
            return shareDomain + "/ul/store/" + storeId;
        }

        return androidStoreUrl;
    }

    /**
     * iOS fallback HTML
     */
    public String iosStoreHtml() {
        return """
                <html><head><meta charset='utf-8'><title>Redirect…</title></head>
                <body style='font-family:sans-serif;'>앱스토어로 이동 중입니다...<script>
                setTimeout(function(){window.location='%s';},300);
                </script></body></html>
                """.formatted(iosStoreUrl);
    }

    /**
     * 카카오 인앱 대응 HTML
     */
    public String buildFallbackHtml(Long storeId, boolean isAndroid, boolean isIOS) {
        if (isAndroid) {
            return """
                    <html><head><meta charset='utf-8'><title>Launching App…</title></head>
                    <body style='font-family:sans-serif;'>앱을 여는 중입니다...
                    <script>
                      setTimeout(function() {
                        window.location.href = 'intent://store/%d#Intent;scheme=https;package=%s;S.browser_fallback_url=%s;end;';
                      }, 100);
                    </script>
                    </body></html>
                    """.formatted(storeId, androidPackage, androidStoreUrl);
        }

        if (isIOS) {
            return """
                    <html><head><meta charset='utf-8'><title>Launching App…</title></head>
                    <body style='font-family:sans-serif;'>앱을 여는 중입니다...
                    <script>
                      setTimeout(function() {
                        window.location = 'https://com.spirit.drinkToday://store/%d';
                      }, 100);
                      setTimeout(function() {
                        window.location = '%s';
                      }, 2000);
                    </script>
                    </body></html>
                    """.formatted(storeId, iosStoreUrl);
        }

        return """
                <html><head><meta charset='utf-8'></head>
                <body><script>window.location.href='%s';</script></body></html>
                """.formatted(androidStoreUrl);
    }
}
