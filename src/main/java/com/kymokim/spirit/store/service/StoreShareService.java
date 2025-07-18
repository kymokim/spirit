package com.kymokim.spirit.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
     * 앱 내부 Deep Link 스킴 (앱과 반드시 일치)
     * 예: spirit://store/{id}
     */
    @Value("${app.scheme:spirit}")
    private String appScheme;

    /**
     * 공유 링크 생성 (Universal Link)
     */
    public String buildShareLink(Long storeId) {
        return UriComponentsBuilder.fromUriString(shareDomain)
                .path("/ul/store/")
                .path(storeId.toString())
                .build()
                .toUriString();
    }

    /**
     * 일반 브라우저 리다이렉션 대상
     */
    public String getRedirectTarget(Long storeId, String userAgent) {
        String ua = userAgent != null ? userAgent.toLowerCase() : "";
        boolean isAndroid = ua.contains("android");
        boolean isIOS = ua.contains("iphone") || ua.contains("ipad");

        if (isAndroid) {
            // intent:// with encoded fallback
            String play = URLEncoder.encode(androidStoreUrl, StandardCharsets.UTF_8);
            return "intent://store/" + storeId +
                    "#Intent;scheme=" + appScheme +
                    ";package=" + androidPackage +
                    ";S.browser_fallback_url=" + play +
                    ";end;";
        }

        if (isIOS) {
            // Universal Link → iOS 시스템이 앱 or Safari
            return shareDomain + "/ul/store/" + storeId;
        }

        return androidStoreUrl;
    }

    /**
     * iOS Universal Link 접속 후 (앱이 안 열렸을 때) 스토어 유도
     */
    public String iosStoreHtml() {
        return """
            <html><head><meta charset='utf-8'><title>Redirect…</title></head>
            <body style='font-family:sans-serif;'>
              앱스토어로 이동 중입니다...
              <script>setTimeout(function(){window.location='%s';},300);</script>
            </body></html>
            """.formatted(iosStoreUrl);
    }

    /**
     * 카카오 인앱용 HTML (플랫폼은 JS로 최종 감지)
     */
    public String buildKakaoInAppHtml(Long storeId) {
        // 자바에서 값 주입
        String deeplink = appScheme + "://store/" + storeId;

        // intent URI (Android only)
        String intentUri = "intent://store/" + storeId
                + "#Intent;scheme=" + appScheme
                + ";package=" + androidPackage
                + ";S.browser_fallback_url=" + URLEncoder.encode(androidStoreUrl, StandardCharsets.UTF_8)
                + ";end;";

        return """
            <html>
            <head>
              <meta charset='utf-8'>
              <meta name='viewport' content='width=device-width, initial-scale=1'>
              <title>Launching App…</title>
            </head>
            <body style='font-family:sans-serif;text-align:center;padding-top:4rem;'>
              앱을 여는 중입니다...<br/>잠시만 기다려 주세요.
              <script>
                (function() {
                  var ua = navigator.userAgent.toLowerCase();
                  var isAndroid = ua.indexOf('android') > -1;
                  var isIOS = /iphone|ipad|ipod/.test(ua);
                  
                  function openDeeplink() {
                    if (isAndroid) {
                      // 1) 앱 스킴 시도
                      window.location = "%1$s";
                      // 2) intent:// (일부 인앱 처리 가능)
                      setTimeout(function(){ window.location = "%2$s"; }, 150);
                      // 3) PlayStore fallback
                      setTimeout(function(){ window.location = "%3$s"; }, 2000);
                    } else if (isIOS) {
                      // iOS 직접 스킴
                      window.location = "%1$s";
                      // App Store fallback
                      setTimeout(function(){ window.location = "%4$s"; }, 1500);
                    } else {
                      // 기타 브라우저 → Android 스토어
                      window.location = "%3$s";
                    }
                  }
                  
                  // 카톡 인앱이 window.location 차단하는 경우 대비, iframe 시도
                  function openViaIframe(url) {
                    var iframe = document.createElement('iframe');
                    iframe.style.display = 'none';
                    iframe.src = url;
                    document.body.appendChild(iframe);
                  }
                  
                  // 첫 시도: iframe (일부 인앱 제한 우회)
                  openViaIframe("%1$s");
                  // 보조 시도
                  setTimeout(openDeeplink, 100);
                })();
              </script>
            </body>
            </html>
            """.formatted(
                deeplink,       // %1$s
                intentUri,      // %2$s
                androidStoreUrl,// %3$s
                iosStoreUrl     // %4$s
        );
    }
}
