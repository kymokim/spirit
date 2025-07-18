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

    /**
     * 앱 내부 Deep Link 스킴 (앱과 반드시 일치) 예: spirit://store/{id}
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
            String play = URLEncoder.encode(androidStoreUrl, StandardCharsets.UTF_8);
            return "intent://store/" + storeId +
                    "#Intent;scheme=" + appScheme +
                    ";package=" + androidPackage +
                    ";S.browser_fallback_url=" + play +
                    ";end;";
        }
        if (isIOS) {
            // iOS 인앱 브라우저 → Universal Link로 재시도
            return shareDomain + "/ul/store/" + storeId;
        }
        // 기타 환경: Android 스토어로
        return androidStoreUrl;
    }

    /**
     * iOS Universal Link fallback HTML
     */
    public String iosStoreHtml() {
        // 템플릿 토큰 치환
        return HTML_IOS_STORE_TEMPLATE
                .replace("${IOS_STORE}", iosStoreUrl);
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

        return HTML_INAPP_TEMPLATE
                .replace("${DEEPLINK}", deeplink)
                .replace("${INTENT_URI}", intentUri)
                .replace("${ANDROID_STORE}", androidStoreUrl)
                .replace("${IOS_STORE}", iosStoreUrl);
    }

    /**
     * iOS Store Fallback 템플릿
     */
    private static final String HTML_IOS_STORE_TEMPLATE = """
            <html lang='ko'>
              <head>
                <meta charset='utf-8'>
                <meta name='viewport' content='width=device-width, initial-scale=1'>
                <title>Redirect…</title>
                <style>
                  :root {
                    --accent:#45C9FF;
                    --radius:12px;
                  }
                  @media (prefers-color-scheme:dark){
                    body{background:#000;color:#fff;}
                    .card{background:#111;color:#fff;}
                  }
                  @media (prefers-color-scheme:light){
                    body{background:#f5f7fa;color:#111;}
                    .card{background:#fff;color:#111;box-shadow:0 2px 8px rgba(0,0,0,.08);}
                  }
                  body{
                    margin:0;
                    font-family:-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Pretendard,sans-serif;
                    display:flex;
                    justify-content:center;
                    align-items:center;
                    min-height:100dvh;
                  }
                  .card{
                    max-width:320px;
                    width:90%;
                    padding:2.5rem 1.5rem 3rem;
                    border-radius:var(--radius);
                    text-align:center;
                  }
                  h1{margin:0 0 .75rem;font-size:1.25rem;font-weight:600;}
                  p{margin:0 0 1.5rem;font-size:.95rem;line-height:1.35;}
                  .spinner{display:inline-block;width:32px;height:32px;position:relative;}
                  .spinner span{
                    position:absolute;
                    width:6px;height:6px;
                    background:var(--accent);
                    border-radius:50%;
                    animation:dotWave 1.2s infinite ease-in-out both;
                  }
                  .spinner span:nth-child(1){left:0;animation-delay:-.24s;}
                  .spinner span:nth-child(2){left:8px;animation-delay:-.12s;}
                  .spinner span:nth-child(3){left:16px;animation-delay:0s;}
                  @keyframes dotWave{
                    0%,80%,100%{transform:scale(0);}
                    40%{transform:scale(1);}
                  }
                  .btn{
                    display:inline-block;
                    margin-top:1rem;
                    padding:.75rem 1.5rem;
                    font-size:1rem;
                    font-weight:600;
                    color:#fff;
                    background:var(--accent);
                    border:none;
                    border-radius:var(--radius);
                    text-decoration:none;
                  }
                </style>
              </head>
              <body>
                <div class='card'>
                  <h1>앱스토어로 이동 중…</h1>
                  <p>잠시만 기다려 주세요.<br>자동으로 이동되지 않으면 아래 버튼을 눌러 주세요.</p>
                  <div class='spinner' aria-hidden='true'><span></span><span></span><span></span></div>
                  <a class='btn' href='${IOS_STORE}'>App Store 열기</a>
                </div>
                <script>
                  setTimeout(function(){window.location='${IOS_STORE}';},300);
                </script>
              </body>
            </html>
            """;

    /**
     * 인앱 브라우저용 UI 템플릿
     */
    private static final String HTML_INAPP_TEMPLATE = """
            <html lang='ko'>
            <head>
              <meta charset='utf-8'>
              <meta name='viewport' content='width=device-width, initial-scale=1'>
              <title>Launching App…</title>
              <style>
                :root {
                  --accent:#45C9FF;
                  --radius:12px;
                }
                @media (prefers-color-scheme:dark){
                  body{background:#000;color:#fff;}
                  .card{background:#111;color:#fff;}
                  .sub{color:rgba(255,255,255,.65);}
                  .fallback{text-decoration:underline;color:var(--accent);}
                }
                @media (prefers-color-scheme:light){
                  body{background:#f5f7fa;color:#111;}
                  .card{background:#fff;color:#111;box-shadow:0 2px 8px rgba(0,0,0,.08);}
                  .sub{color:rgba(0,0,0,.65);}
                  .fallback{text-decoration:underline;color:var(--accent);}
                }
                body{
                  margin:0;
                  font-family:-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Pretendard,sans-serif;
                  display:flex;
                  justify-content:center;
                  align-items:center;
                  min-height:100dvh;
                }
                .card{
                  max-width:360px;
                  width:90%;
                  padding:2.75rem 1.75rem 3.25rem;
                  border-radius:var(--radius);
                  text-align:center;
                }
                h1{margin:0 0 .75rem;font-size:1.25rem;font-weight:600;}
                p{margin:0 0 1.25rem;font-size:.95rem;line-height:1.4;}
                .sub{font-size:.85rem;margin-top:.5rem;}
                .spinner{display:inline-block;width:32px;height:32px;position:relative;margin-top:1rem;}
                .spinner span{
                  position:absolute;
                  width:6px;height:6px;
                  background:var(--accent);
                  border-radius:50%;
                  animation:dotWave 1.2s infinite ease-in-out both;
                }
                .spinner span:nth-child(1){left:0;animation-delay:-.24s;}
                .spinner span:nth-child(2){left:8px;animation-delay:-.12s;}
                .spinner span:nth-child(3){left:16px;animation-delay:0s;}
                @keyframes dotWave{
                  0%,80%,100%{transform:scale(0);}
                  40%{transform:scale(1);}
                }
                .btn{
                  display:inline-block;
                  margin-top:1.75rem;
                  padding:.9rem 1.75rem;
                  font-size:1.05rem;
                  font-weight:700;
                  color:#fff;
                  background:var(--accent);
                  border:none;
                  border-radius:var(--radius);
                  text-decoration:none;
                }
                #copyLink{
                  margin-top:.75rem;
                  font-size:.85rem;
                  background:none;
                  border:none;
                  color:var(--accent);
                  text-decoration:underline;
                }
              </style>
            </head>
            <body>
              <div class='card'>
                <h1>앱을 여는 중입니다…</h1>
                <p>잠시만 기다려 주세요.<br>열리지 않으면 아래 버튼을 눌러 직접 실행해 주세요.</p>
                <div class='spinner' aria-hidden='true'><span></span><span></span><span></span></div>
                <button class='btn' onclick="openApp()">앱 열기</button>
                <p class='sub'>문제가 계속되면 스토어에서 앱을 설치한 뒤 다시 시도해 주세요.<br>
                  <a class='fallback' id='storeLink' href='#'>스토어로 이동</a>
                </p>
                <button id='copyLink' type='button'>딥링크 복사</button>
              </div>
              <script>
                (function() {
                  var deeplink = "${DEEPLINK}";
                  var intentUri = "${INTENT_URI}";
                  var playUrl = "${ANDROID_STORE}";
                  var iosUrl = "${IOS_STORE}";
                  var ua = navigator.userAgent.toLowerCase();
                  var isAndroid = ua.indexOf('android') > -1;
                  var isIOS = /iphone|ipad|ipod/.test(ua);
            
                  // 스토어 링크 클릭 시 플랫폼별 이동
                  var storeLink = document.getElementById('storeLink');
                  if (storeLink) {
                    storeLink.addEventListener('click', function(e){
                      e.preventDefault();
                      window.location = isIOS ? iosUrl : playUrl;
                    });
                  }
            
                  // 딥링크 복사 (미지원 브라우저는 숨김)
                  var copyBtn = document.getElementById('copyLink');
                  if (copyBtn && navigator.clipboard) {
                    copyBtn.addEventListener('click', function(){
                      navigator.clipboard.writeText(deeplink).then(function(){
                        copyBtn.textContent = '복사 완료!';
                        setTimeout(function(){copyBtn.textContent='딥링크 복사';},2000);
                      });
                    });
                  } else if (copyBtn) {
                    copyBtn.style.display='none';
                  }
            
                  window.openApp = tryOpen;
            
                  function tryOpen() {
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
            
                  // 자동 시도 (약간 지연)
                  setTimeout(tryOpen, 100);
                })();
              </script>
            </body>
            </html>
            """;
}
