package com.kymokim.spirit.link.controller;

import com.kymokim.spirit.link.dto.LinkData;
import com.kymokim.spirit.link.dto.PathType;
import com.kymokim.spirit.link.service.TemplateProvider;
import com.kymokim.spirit.link.service.LinkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Tag(name = "Link API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class LinkController {

    private final LinkService linkService;
    private final TemplateProvider templateProvider;

    @GetMapping("/link/{type}/{id}")
    public ResponseEntity<?> redirectByOS(@PathVariable String type, @PathVariable String id,
                                          @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        return linkService.redirectByUserAgent(LinkData.PathData.builder().type(PathType.fromUrl(type)).id(id).build(), userAgent);
    }

    @GetMapping("/ul/**")
    public void iosFallback(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(templateProvider.iosStoreHtml());
    }

    @GetMapping(value = "/.well-known/assetlinks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAndroidAssetLinks() {
        return templateProvider.androidAssetLinks();
    }

    @GetMapping(value = "/.well-known/apple-app-site-association", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAppleAppSiteAssociation() {
        return templateProvider.appleAppSiteAssociation();
    }
}
