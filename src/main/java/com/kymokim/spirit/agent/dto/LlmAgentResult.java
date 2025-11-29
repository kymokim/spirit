package com.kymokim.spirit.agent.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmAgentResult {

    private String responseId;
    private String agentMessage;
    private AgentMode agentMode;
    private SearchConditions searchConditions;
}
