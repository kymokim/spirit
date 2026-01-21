package com.kymokim.spirit.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportTarget {
    STORE("매장"),
    POST("게시글"),
    COMMENT("댓글");

    private final String displayName;
}
