package com.kymokim.spirit.link.service;

import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.config.LinkConfig;
import com.kymokim.spirit.link.dto.LinkData;
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
        boolean isAndroid = lowerUserAgent.contains("android");
        boolean isIOS = lowerUserAgent.contains("iphone") || lowerUserAgent.contains("ipad");
        String[] IN_APP_KEYWORDS = {
                "kakaotalk", "instagram", "fbav", "facebook", "messenger", "line", "naver", "tiktok", "telegram", "whatsapp"
        };

        boolean isInApp = false;
        for (String keyword : IN_APP_KEYWORDS) {
            if (lowerUserAgent.contains(keyword)) {
                isInApp = true;
                break;
            }
        }
        if (isInApp) {
            String html = templateProvider.inAppHtml(pathData);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(html);
        }

        String url  = linkConfig.getAndroidStoreUrl();
        if (isAndroid) {
            url = linkBuilder.intentLink(pathData);
        }
        if (isIOS) {
            url = linkBuilder.universalLink(pathData);
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }
}
