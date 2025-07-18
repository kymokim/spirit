package com.kymokim.spirit.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

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

    /** 앱 내부 Deep Link 스킴 (앱과 반드시 일치) 예: spirit://store/{id} */
    @Value("${app.scheme:spirit}")
    private String appScheme;

    /** 공유 링크 생성 (Universal Link) */
    public String buildShareLink(Long storeId) {
        return UriComponentsBuilder.fromUriString(shareDomain)
                .path("/ul/store/")
                .path(storeId.toString())
                .build()
                .toUriString();
    }

    /** 일반 브라우저 리다이렉션 대상 */
    public String getRedirectTarget(Long storeId, String userAgent) {
        String ua = userAgent != null ? userAgent.toLowerCase() : "";
        boolean isAndroid = ua.contains("android");
        boolean isIOS = ua.contains("iphone") || ua.contains("ipad");

        if (isAndroid) {
            String play = URLEncoder.encode(androidStoreUrl, StandardCharsets.UTF_8);
            return "intent://store/" + storeId +
                    "#Intent;scheme=" + appScheme +
                    ";package=" + androidPackage +
                    ";S.browser_fallback_url=" + play +
                    ";end;";
        }
        if (isIOS) {
            return shareDomain + "/ul/store/" + storeId;
        }
        return androidStoreUrl;
    }

    /** iOS Universal Link fallback HTML */
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
     * 외부 인앱 브라우저 공통 HTML (KakaoTalk, Instagram, Facebook, LINE, Naver 등)
     * JS에서 UA 재검사 후 플랫폼별 딥링크 + 스토어 fallback.
     */
    public String buildInAppHtml(Long storeId) {
        String deeplink = appScheme + "://store/" + storeId;
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
              <style>
                body {font-family:sans-serif;text-align:center;padding-top:4rem;}
                button{margin-top:2rem;padding:.75rem 1.5rem;font-size:1rem;}
              </style>
            </head>
            <body>
              <div>앱을 여는 중입니다...<br/>열리지 않으면 아래 버튼을 눌러주세요.</div>
              <button onclick="openApp()">앱 열기</button>
              <script>
                (function() {
                  var deeplink = "%1$s";
                  var intentUri = "%2$s";
                  var playUrl = "%3$s";
                  var iosUrl = "%4$s";
                  var ua = navigator.userAgent.toLowerCase();
                  var isAndroid = ua.indexOf('android') > -1;
                  var isIOS = /iphone|ipad|ipod/.test(ua);

                  window.openApp = tryOpen;

                  function tryOpen() {
                    var start = Date.now();
                    if (isAndroid) {
                      openViaIframe(deeplink);
                      setTimeout(function(){ window.location = intentUri; }, 200);
                      setTimeout(function(){ window.location = playUrl; }, 1800);
                    } else if (isIOS) {
                      window.location = deeplink;
                      setTimeout(function(){ window.location = iosUrl; }, 1500);
                    } else {
                      window.location = playUrl;
                    }
                  }

                  function openViaIframe(url) {
                    var iframe = document.createElement('iframe');
                    iframe.style.display = 'none';
                    iframe.src = url;
                    document.body.appendChild(iframe);
                  }

                  // 자동 시도
                  setTimeout(tryOpen, 100);
                })();
              </script>
            </body>
            </html>
            """.formatted(
                deeplink,
                intentUri,
                androidStoreUrl,
                iosStoreUrl
        );
    }
}
