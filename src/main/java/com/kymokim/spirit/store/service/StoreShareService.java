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

    /* ===== 설정 값 ===== */
    @Value("${app.share.domain:https://dev.team-spirit.click}")
    private String shareDomain;

    @Value("${app.android.package:com.spiritfront}")
    private String androidPackage;

    @Value("${app.store.fallback.android:https://play.google.com/store/apps/details?id=com.spiritfront}")
    private String androidStoreUrl;

    @Value("${app.store.fallback.ios:https://apps.apple.com/kr/app/%ED%95%9C%EC%9E%94%ED%95%A0%EA%B9%8C/id6740095371}")
    private String iosStoreUrl;

    /* ===== 공유 링크 생성 ===== */
    public String buildShareLink(Long storeId) {
        return UriComponentsBuilder.fromUriString(shareDomain)
                .path("/link/store/")
                .path(storeId.toString())
                .build()
                .toUriString();
    }

    /* ===== Android intent:// 링크 생성 ===== */
    public String buildAndroidIntent(Long storeId) {
        String play = URI.create(androidStoreUrl).toString();
        return "intent://store/" + storeId +
                "#Intent;scheme=https;package=" + androidPackage +
                ";S.browser_fallback_url=" + play + ";end;";
    }

    /* ===== iOS 웹 fallback HTML ===== */
    public String iosStoreHtml() {
        return """
               <!doctype html><html lang="ko"><meta charset="utf-8">
               <title>한잔할까 열기</title>
               <body style='font-family:sans-serif;'>앱스토어로 이동 중입니다...
               <script>
                 // Universal Link 실패 시 App Store 이동
                 setTimeout(function(){ window.location = '%s'; }, 1500);
               </script>
               </body></html>
               """.formatted(iosStoreUrl);
    }
}
