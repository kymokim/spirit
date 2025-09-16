package com.kymokim.spirit.link.service;

import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.config.LinkConfig;
import com.kymokim.spirit.link.dto.LinkData;
import com.kymokim.spirit.store.entity.ManagerInvitation;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.repository.ManagerInvitationRepository;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@MainTransactional
public class TemplateProvider {

    private final LinkBuilder linkBuilder;
    private final LinkConfig linkConfig;
    private final StoreRepository storeRepository;
    private final ManagerInvitationRepository managerInvitationRepository;

    public String inAppHtml(LinkData.PathData pathData) {
        LinkData.MetaData metaData = LinkData.MetaData.builder().build();
        switch (pathData.getType()) {
            case STORE -> {
                metaData.setDescription("매장 정보");
                Store store = storeRepository.findById(Long.valueOf(pathData.getId())).orElse(null);
                if (store != null) {
                    metaData.setTitle(store.getName() + " | 한잔할까");
                    if (!Objects.equals(store.getMainImgUrl(), null))
                        metaData.setImage(store.getMainImgUrl());
                }
            }
            case STORE_MANAGER_INVITE -> {
                metaData.setDescription("운영진 초대");
                ManagerInvitation managerInvitation = managerInvitationRepository.findById(pathData.getId()).orElse(null);
                if (managerInvitation != null) {
                    metaData.setTitle(managerInvitation.getStoreName() + " | 한잔할까");
                    if (!Objects.equals(managerInvitation.getStoreImage(), null))
                        metaData.setImage(managerInvitation.getStoreImage());
                }
            }
        }
        return inAppHtml(pathData, metaData);
    }

    public String inAppStoreHtml(String agent) {
        if (agent.equals("ios"))
            return iosStoreHtml();
        return androidStoreHtml();
    }

    private String inAppHtml(LinkData.PathData pathData, LinkData.MetaData metaData) {

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
                        return /iphone|ipad|macintosh/.test(ua);
                      }
                      function isAndroid() {
                        return navigator.userAgent.toLowerCase().indexOf('android') > -1;
                      }
                
                      document.addEventListener('DOMContentLoaded', function() {
                        document.getElementById('storeLink').href = isIOS() ? iosUrl : playUrl;
                        setTimeout(tryOpen, 150);
                      });
                
                      window.openApp = tryOpen;
                
                      function tryOpen() {
                        if (isAndroid()) {
                          window.location.href = intentUri;
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
                linkBuilder.deepLink(pathData),
                linkBuilder.intentLink(pathData),
                linkConfig.getAndroidStoreUrl(),
                linkConfig.getIosStoreUrl(),
                metaData.getTitle(),
                metaData.getDescription(),
                metaData.getImage()
        );
    }

    public String iosStoreHtml() {
        return """
                <!doctype html>
                <html lang='ko'>
                <head>
                  <meta charset='utf-8'>
                  <meta name='viewport' content='width=device-width, initial-scale=1'>
                  <meta property="og:title"       content="%2$s"/>
                  <meta property="og:description" content="%3$s"/>
                  <meta property="og:image"       content="%4$s"/>
                  <meta property="og:image:width" content="1200"/>
                  <meta property="og:image:height" content="630"/>
                  <meta property="og:image"       content="%5$s"/>
                  <meta property="og:image:width" content="1200"/>
                  <meta property="og:image:height" content="1200"/>
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
                """.formatted(
                linkConfig.getIosStoreUrl(),
                "한잔할까 설치",
                "한잔하고 싶은 당신을 위한 서비스",
                "https://spirit19-bucket.s3.ap-northeast-2.amazonaws.com/app/thumbnail_1200x630.png",
                "https://spirit19-bucket.s3.ap-northeast-2.amazonaws.com/app/thumbnail_1200x1200.png"
        );
    }

    public String androidStoreHtml() {
        return """
                <!doctype html>
                <html lang='ko'>
                <head>
                  <meta charset='utf-8'>
                  <meta name='viewport' content='width=device-width, initial-scale=1'>
                  <meta property="og:title"       content="%2$s"/>
                  <meta property="og:description" content="%3$s"/>
                  <meta property="og:image"       content="%4$s"/>
                  <meta property="og:image:width" content="1200"/>
                  <meta property="og:image:height" content="630"/>
                  <meta property="og:image"       content="%5$s"/>
                  <meta property="og:image:width" content="1200"/>
                  <meta property="og:image:height" content="1200"/>
                  <title>Play Store Redirect</title>
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
                    <div>Play Store로 이동 중입니다.</div>
                    <div class='spinner'></div>
                    <div class='hint'>잠시만 기다려 주세요.<br/>자동으로 이동되지 않으면 아래 버튼을 눌러주세요.</div>
                    <a class='btn-store' href='%1$s' rel='noopener'>Play Store 열기</a>
                  </div>
                  <script>
                    setTimeout(function(){
                      window.location.href='%1$s';
                    },300);
                  </script>
                </body>
                </html>
                """.formatted(
                linkConfig.getAndroidStoreUrl(),
                "한잔할까 설치",
                "한잔하고 싶은 당신을 위한 서비스",
                "https://spirit19-bucket.s3.ap-northeast-2.amazonaws.com/app/thumbnail_1200x630.png",
                "https://spirit19-bucket.s3.ap-northeast-2.amazonaws.com/app/thumbnail_1200x1200.png"
        );
    }

    public String androidAssetLinks() {
        return """
                [
                  {
                    "relation": ["delegate_permission/common.handle_all_urls"],
                    "target": {
                      "namespace": "android_app",
                      "package_name": "com.spiritfront",
                      "sha256_cert_fingerprints": [
                        "F1:55:DB:49:4D:C6:CE:04:97:42:1E:AC:F0:88:61:90:6C:3E:90:0D:AE:26:72:C1:4B:D7:D8:3D:37:C1:4B:88"
                      ]
                    }
                  }
                ]
                """;
    }

    public Map<String, Object> appleAppSiteAssociation() {
        return Map.of(
                "applinks", Map.of(
                        "apps", List.of(),
                        "details", List.of(
                                Map.of(
                                        "appID", "B6Y8UPNX32.com.spirit.drinkToday",
                                        "paths", List.of(
                                                "/ul/store/*",
                                                "/ul/store-manager-invite/*"
                                        )
                                )
                        )
                )
        );
    }
}
