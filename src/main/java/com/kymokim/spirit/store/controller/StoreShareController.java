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
}
