package com.kymokim.spirit.link.dto;

import com.kymokim.spirit.common.exception.CommonErrorCode;
import com.kymokim.spirit.common.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PathType {
    STORE("store"),
    STORE_MANAGER_INVITE("store-manager-invite"),
    INSTALL("install");

    private final String url;

    public static PathType fromUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new CustomException(CommonErrorCode.EMPTY_PARAMETER, "type");
        }
        for (PathType type : values()) {
            if (type.url.equalsIgnoreCase(url)) {
                return type;
            }
        }
        throw new CustomException(CommonErrorCode.INVALID_PARAMETER, "type=" + url);
    }
}
