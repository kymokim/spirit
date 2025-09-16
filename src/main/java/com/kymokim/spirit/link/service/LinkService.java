package com.kymokim.spirit.link.service;

import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.config.LinkConfig;
import com.kymokim.spirit.link.dto.LinkData;
import com.kymokim.spirit.link.dto.PathType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@MainTransactional
public class LinkService {

    private final TemplateProvider templateProvider;
    private final LinkBuilder linkBuilder;
    private final LinkConfig linkConfig;

    public ResponseEntity<?> redirectByUserAgent(LinkData.PathData pathData, String userAgent) {

        String lowerUserAgent = userAgent != null ? userAgent.toLowerCase() : "";
        boolean isInApp = isInApp(lowerUserAgent);
        String agent = detectAgent(lowerUserAgent);

        if (pathData.getType().equals(PathType.INSTALL))
            return redirectToStore(isInApp, agent);

        if (isInApp) {
            String html = templateProvider.inAppHtml(pathData);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(html);
        }

        String url = linkConfig.getAndroidStoreUrl();
        if (agent.equals("android")) {
            url = linkBuilder.intentLink(pathData);
        }
        if (agent.equals("ios")) {
            url = linkBuilder.universalLink(pathData);
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    private ResponseEntity<?> redirectToStore(boolean isInApp, String agent) {
        if (isInApp) {
            String html = templateProvider.inAppStoreHtml(agent);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(html);
        }

        String url = linkConfig.getAndroidStoreUrl();
        if (agent.equals("ios")) {
            url = linkConfig.getIosStoreUrl();
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    private boolean isInApp(String lowerUserAgent) {
        String[] IN_APP_KEYWORDS = {
                "kakaotalk", "instagram", "fbav", "facebook", "messenger", "line", "naver", "tiktok", "telegram", "whatsapp"
        };
        for (String keyword : IN_APP_KEYWORDS) {
            if (lowerUserAgent.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String detectAgent(String lowerUserAgent) {
        if (lowerUserAgent.contains("android"))
            return "android";
        if (lowerUserAgent.contains("iphone") || lowerUserAgent.contains("ipad") || lowerUserAgent.contains("macintosh"))
            return "ios";
        return "etc";
    }
}
