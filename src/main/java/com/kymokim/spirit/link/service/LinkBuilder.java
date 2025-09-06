package com.kymokim.spirit.link.service;

import com.kymokim.spirit.common.config.LinkConfig;
import com.kymokim.spirit.link.dto.LinkData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class LinkBuilder {

    private final LinkConfig linkConfig;

    // https://team-spirit.click/link/{type}/{id}
    public String serverLink(LinkData.PathData pathData) {
        return UriComponentsBuilder.fromUriString(linkConfig.getShareDomain())
                .path("/link/")
                .path(pathData.getPath())
                .build()
                .toUriString();
    }

    // https://team-spirit.click/ul/{type}/{id}
    public String universalLink(LinkData.PathData pathData) {
        return linkConfig.getShareDomain() + "/ul/" + pathData.getPath();
    }

    // spirit://{type}/{id}
    public String deepLink(LinkData.PathData pathData) {
        return linkConfig.getAppScheme() + "://" + pathData.getPath();
    }

    // intent://{type}/{id}#Intent;scheme=spirit;package=com.spiritfront;S.browser_fallback_url=https://play.google.com/store/apps/details?id=com.spiritfront;end;
    public String intentLink(LinkData.PathData pathData) {
        String fallback = URLEncoder.encode(linkConfig.getAndroidStoreUrl(), StandardCharsets.UTF_8);
        return "intent://" + pathData.getPath() +
                "#Intent;scheme=" + linkConfig.getAppScheme() +
                ";package=" + linkConfig.getAndroidPackage() +
                ";S.browser_fallback_url=" + fallback +
                ";end;";
    }
}
