package com.kymokim.spirit.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestAgent {

    @Schema(description = "이전 응답 ID, 이전 응답이 있는 경우에만 입력, 없으면 null", nullable = true)
    private String previousResponseId;
    @NotNull
    @Schema(description = "사용자 위도", example = "37.324583")
    private Double latitude;
    @NotNull
    @Schema(description = "사용자 경도", example = "127.107398")
    private Double longitude;
    @NotBlank
    @Schema(description = "사용자 메시지, 최대 270자(시스템 70자 + 사용자 200자)", example = "소주 가성비 좋은 포장마차 추천해줘.")
    @Size(max = 280, message = "사용자 메시지는 270자 이하로 입력해주세요.")
    private String userMessage;
}
