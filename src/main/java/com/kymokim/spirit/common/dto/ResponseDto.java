package com.kymokim.spirit.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Getter
@Builder
public class ResponseDto {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String message;@Builder.Default
    private LocalDateTime createAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    private Object data;
}