package com.kymokim.spirit.agent.dto;

import com.kymokim.spirit.common.dto.ResponseLocationDto;
import com.kymokim.spirit.store.dto.ResponseStore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAgent {

    @Schema(description = "에이전트 대화 결과")
    private LlmAgentResult llmAgentResult;
    @Schema(description = "변경된 주소")
    private ResponseLocationDto.GetAddressDto changedAddress;
    @Schema(description = "검색 결과")
    private Page<ResponseStore.SearchStoreDto> stores;
}
