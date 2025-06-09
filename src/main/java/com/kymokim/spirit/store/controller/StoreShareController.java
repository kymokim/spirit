package com.kymokim.spirit.store.controller;

import com.kymokim.spirit.store.service.StoreShareService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StoreShareController {

    private final StoreShareService shareService;

    record ShareLinkResponse(String url) { }

    @GetMapping("/api/store/share/{storeId}")
    public ResponseEntity<ShareLinkResponse> createShareLink(@PathVariable Long storeId) {
        String url = shareService.buildShareLink(storeId);
        return ResponseEntity.ok(new ShareLinkResponse(url));
    }

    @GetMapping("/link/store/{storeId}")
    public ResponseEntity<Void> redirectByOS(
            @PathVariable Long storeId,
            @RequestHeader(value = "User-Agent", required = false) String ua) {
        String target = shareService.getRedirectTarget(storeId, ua);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, target)
                .build();
    }

    @GetMapping("/ul/store/{storeId}")
    public void iosFallback(@PathVariable Long storeId, HttpServletResponse res) throws IOException {
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
                        "FD:D5:B2:A3:27:63:F3:94:19:D7:6D:A2:02:A6:74:AF:FD:31:28:C8:DD:D5:AB:11:D7:87:7E:5A:AC:30:44:9B"
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
