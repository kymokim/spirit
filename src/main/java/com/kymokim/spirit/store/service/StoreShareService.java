package com.kymokim.spirit.store.service;

import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    private final StoreRepository storeRepository;

    /** 공유 링크 생성 (Universal Link) */
    public String buildShareLink(Long storeId) {
        return UriComponentsBuilder.fromUriString(shareDomain)
                .path("/link/store/")
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

    /** iOS Store Fallback (Safari 등에서 앱 미설치 시) */
    public String iosStoreHtml() {
        return """
            <!doctype html>
            <html lang='ko'>
            <head>
              <meta charset='utf-8'>
              <meta name='viewport' content='width=device-width, initial-scale=1'>
              <title>App Store Redirect</title>
              <style>
                :root {
                  --accent:#45C9FF;
                  --text-main:#222;
                  --text-sub:#666;
                  --bg:#fff;
                }
                body {
                  margin:0;
                  padding:0;
                  background:var(--bg);
                  color:var(--text-main);
                  font-family:system-ui,apple-system,'Helvetica Neue',sans-serif;
                  text-align:center;
                  line-height:1.4;
                }
                .wrap{padding-top:5rem;max-width:320px;margin:0 auto;}
                .spinner{margin:2rem auto;width:32px;height:32px;border-radius:50%%;
                         border:4px solid rgba(69,201,255,.25);border-top-color:var(--accent);
                         animation:spin 1s linear infinite;}
                @keyframes spin{to{transform:rotate(360deg)}}
                .hint{margin-top:1.5rem;color:var(--text-sub);font-size:.9rem;}
                a.btn-store{
                  display:inline-block;margin-top:2rem;padding:.75rem 2rem;
                  font-size:1rem;font-weight:600;color:#fff;background:var(--accent);
                  border:none;border-radius:8px;text-decoration:none;
                }
                a.btn-store:focus{outline:2px solid var(--accent);outline-offset:2px;}
              </style>
            </head>
            <body>
              <div class='wrap'>
                <div>App Store로 이동 중입니다.</div>
                <div class='spinner'></div>
                <div class='hint'>잠시만 기다려 주세요.<br/>자동으로 이동되지 않으면 아래 버튼을 눌러주세요.</div>
                <a class='btn-store' href='%1$s' rel='noopener'>App Store 열기</a>
              </div>
              <script>
                setTimeout(function(){
                  window.location.href='%1$s';
                },300);
              </script>
            </body>
            </html>
            """.formatted(iosStoreUrl);
    }

    /**
     * 외부 인앱 브라우저 공통 HTML (KakaoTalk, Instagram, Facebook, LINE, Naver 등)
     * - 자동으로 딥링크 시도
     */
    public String buildInAppHtml(Long storeId) {
        String deeplink = appScheme + "://store/" + storeId;
        String intentUri = "intent://store/" + storeId
                + "#Intent;scheme=" + appScheme
                + ";package=" + androidPackage
                + ";S.browser_fallback_url=" + URLEncoder.encode(androidStoreUrl, StandardCharsets.UTF_8)
                + ";end;";
        
        String title = "";
        String description = "한잔할까 - 한잔하고 싶은 당신을 위한 서비스";
        String image = "";
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store != null) {
            title = store.getName() + " | 한잔할까";
            if (!Objects.equals(store.getMainImgUrl(), null)) 
                image = store.getMainImgUrl();
        }

        return """
        <!doctype html>
        <html lang='ko'>
        <head>
          <meta charset='utf-8'>
          <meta name='viewport' content='width=device-width, initial-scale=1'>
          <meta property="og:title"       content="%5$s"/>
          <meta property="og:description" content="%6$s"/>
          <meta property="og:image"       content="%7$s"/>
          <title>%5$s</title>
          <style>
            :root {
              --accent:#45C9FF;
              --text-main:#222;
              --text-sub:#666;
              --bg:#fff;
            }
            html,body{margin:0;padding:0;background:var(--bg);color:var(--text-main);
                      font-family:system-ui,apple-system,'Helvetica Neue',sans-serif;}
            .wrap{text-align:center;padding:4rem 1.5rem;max-width:420px;margin:0 auto;}
            h1{font-size:1.25rem;margin:0 0 1rem 0;font-weight:600;}
            p{margin:.5rem 0;font-size:1rem;}
            .hint{margin-top:1rem;font-size:.9rem;color:var(--text-sub);}
            .actions{margin-top:2.5rem;display:flex;flex-direction:column;gap:1rem;}
            button, a.btn-store{
              cursor:pointer;
              padding:.9rem 1.75rem;
              font-size:1rem;
              font-weight:600;
              border-radius:8px;
              border:none;
            }
            button.btn-open{color:#fff;background:var(--accent);}
            a.btn-store{
              display:inline-block;
              color:var(--accent);
              background:rgba(69,201,255,.12);
              text-decoration:none;
            }
            button.btn-open:focus,
            a.btn-store:focus{outline:2px solid var(--accent);outline-offset:2px;}
            /* 로딩 점 애니메이션 */
            .dots{margin:2rem auto 0 auto;display:inline-block;}
            .dots span{
              display:inline-block;width:8px;height:8px;margin:0 3px;background:var(--accent);
              border-radius:50%%;opacity:.4;animation:bounce 1.4s infinite ease-in-out both;
            }
            .dots span:nth-child(1){animation-delay:-.32s}
            .dots span:nth-child(2){animation-delay:-.16s}
            @keyframes bounce{
              0%%,80%%,100%%{transform:scale(0);opacity:.4;}
              40%%{transform:scale(1.1);opacity:1;}
            }
          </style>
        </head>
        <body>
          <div class='wrap'>
            <h1>한잔할까 실행 중</h1>
            <p>잠시만 기다려 주세요.</p>
            <div class='dots'><span></span><span></span><span></span></div>
            <div class='hint'>앱이 실행되지 않으면 아래 버튼을 눌러주세요.</div>
            <div class='actions'>
              <button type='button' class='btn-open' onclick='openApp()'>앱 열기</button>
              <a class='btn-store' id='storeLink' href='#' rel='noopener'>스토어에서 설치</a>
            </div>
          </div>
          <script>
            (function() {
              var deeplink = "%1$s";
              var intentUri = "%2$s";
              var playUrl = "%3$s";
              var iosUrl = "%4$s";

              function isIOS() {
                var ua = navigator.userAgent.toLowerCase();
                return /iphone|ipad|ipod/.test(ua);
              }
              function isAndroid() {
                return navigator.userAgent.toLowerCase().indexOf('android') > -1;
              }

              window.openApp = tryOpen;

              function tryOpen() {
                if (isAndroid()) {
                  openViaIframe(deeplink);
                  setTimeout(function(){ window.location.href = intentUri; }, 200);
                  setTimeout(function(){ window.location.href = playUrl; }, 1800);
                } else if (isIOS()) {
                  window.location.href = deeplink;
                  setTimeout(function(){ window.location.href = iosUrl; }, 1500);
                } else {
                  window.location.href = playUrl;
                }
              }

              function openViaIframe(url) {
                try {
                  var iframe = document.createElement('iframe');
                  iframe.style.display = 'none';
                  iframe.src = url;
                  document.body.appendChild(iframe);
                } catch(e) {
                  console.warn('iframe open failed', e);
                }
              }
            })();
          </script>
        </body>
        </html>
        """.formatted(
                deeplink,
                intentUri,
                androidStoreUrl,
                iosStoreUrl,
                title,
                description,
                image
        );
    }

}
