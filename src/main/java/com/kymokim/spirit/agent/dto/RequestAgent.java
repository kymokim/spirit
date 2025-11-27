package com.kymokim.spirit.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestAgent {

    @NotBlank
    @Schema(description = "대화 세션 id")
    private String sessionId;
    @NotNull
    @Schema(description = "사용자 위도", example = "37.324583")
    private Double latitude;
    @NotNull
    @Schema(description = "사용자 경도", example = "127.107398")
    private Double longitude;
    @NotBlank
    @Schema(description = "사용자 메시지", example = "조용한 와인바 추천해줘")
    private String userMessage;
}
