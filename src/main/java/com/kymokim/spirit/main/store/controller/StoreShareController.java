package com.kymokim.spirit.main.store.controller;

import com.kymokim.spirit.main.store.service.StoreShareService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StoreShareController {

    private final StoreShareService shareService;

    // 인앱 브라우저 UA 키워드 (소문자 비교)
    private static final String[] INAPP_KEYWORDS = {
            "kakaotalk", "instagram", "fbav", "facebook", "messenger",
            "line", "naver", "tiktok", "telegram", "whatsapp"
    };

    record ShareLinkResponse(String url) { }

    @GetMapping("/api/store/share/{storeId}")
    public ResponseEntity<ShareLinkResponse> createShareLink(@PathVariable Long storeId) {
        String url = shareService.buildShareLink(storeId);
        return ResponseEntity.ok(new ShareLinkResponse(url));
    }

    @GetMapping("/link/store/{storeId}")
    public ResponseEntity<?> redirectByOS(
            @PathVariable Long storeId,
            @RequestHeader(value = "User-Agent", required = false) String ua) {
        if (isInApp(ua)) {
            String html = shareService.buildInAppHtml(storeId);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(html);
        }

        String target = shareService.getRedirectTarget(storeId, ua);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, target)
                .build();
    }

    private static boolean isInApp(String ua) {
        String lowerUa = ua != null ? ua.toLowerCase() : "";

        boolean isInApp = false;
        for (String kw : INAPP_KEYWORDS) {
            if (lowerUa.contains(kw)) {
                isInApp = true;
                break;
            }
        }

        // 2) 선택적: generic WebView 감지 (Android)
        // ex) UA에 "; wv)" 포함 & 크롬 외부 탭이 아닌 경우
        if (!isInApp && lowerUa.contains("; wv)")) {
            isInApp = true;
        }
        return isInApp;
    }

    /** Universal Link landing (앱 미실행 시 스토어 안내) */
    @GetMapping("/ul/store/{storeId}")
    public void iosFallback(@PathVariable Long storeId, HttpServletResponse res) throws IOException {
        log.info("[StoreShare] /ul/store/{} landing", storeId);
        res.setContentType(MediaType.TEXT_HTML_VALUE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.getWriter().write(shareService.iosStoreHtml());
    }

    @GetMapping(value = "/.well-known/assetlinks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAndroidAssetLinks() {
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

    @GetMapping(value = "/.well-known/apple-app-site-association", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAppleAppSiteAssociation() {
        return Map.of(
                "applinks", Map.of(
                        "apps", List.of(),
                        "details", List.of(
                                Map.of(
                                        "appID", "B6Y8UPNX32.com.spirit.drinkToday",
                                        "paths", List.of("/ul/store/*")
                                )
                        )
                )
        );
    }
}