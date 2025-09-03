package com.kymokim.spirit.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Getter
@Setter
public class LinkConfig {

    @Value("${app.share.domain:https://dev.team-spirit.click}")
    public String shareDomain;

    @Value("${app.android.package:com.spiritfront}")
    public String androidPackage;

    @Value("${app.store.fallback.android:https://play.google.com/store/apps/details?id=com.spiritfront}")
    public String androidStoreUrl;

    @Value("${app.store.fallback.ios:https://apps.apple.com/kr/app/%ED%95%9C%EC%9E%94%ED%95%A0%EA%B9%8C/id6740095371}")
    public String iosStoreUrl;

    @Value("${app.scheme:spirit}")
    public String appScheme;
}
