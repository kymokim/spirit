package com.kymokim.spirit.agent.controller;

import com.kymokim.spirit.agent.dto.RequestAgent;
import com.kymokim.spirit.agent.dto.ResponseAgent;
import com.kymokim.spirit.agent.service.AgentService;
import com.kymokim.spirit.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agent API")
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/chat-search")
    public ResponseEntity<ResponseDto> chatSearch(@Valid @RequestBody RequestAgent request) {
        ResponseAgent response = agentService.chatSearch(request);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Agent chat search executed successfully.")
                .data(response)
                .build();
        return ResponseEntity.ok(responseDto);
    }
}
