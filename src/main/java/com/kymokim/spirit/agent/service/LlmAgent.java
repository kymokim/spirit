package com.kymokim.spirit.agent.service;

import com.kymokim.spirit.agent.dto.RequestAgent;
import com.kymokim.spirit.agent.dto.LlmAgentResult;

public interface LlmAgent {
    LlmAgentResult request(RequestAgent requestAgent);
}
