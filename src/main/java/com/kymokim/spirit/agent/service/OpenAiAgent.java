package com.kymokim.spirit.agent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kymokim.spirit.common.config.OpenAiConfig;
import com.kymokim.spirit.agent.dto.LlmAgentResult;
import com.kymokim.spirit.agent.dto.RequestAgent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiAgent implements LlmAgent {

    private static final String RESPONSES_PATH = "/responses";

    private final OpenAiConfig openAiConfig;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    @Override
    public LlmAgentResult request(RequestAgent requestAgent) {
        if (!StringUtils.hasText(openAiConfig.getApiKey())) {
            throw new IllegalStateException("OpenAI API 키가 설정되지 않았습니다.");
        }

        log.info("OpenAI 요청 시작: timeout={}s", openAiConfig.getTimeout().toSeconds());
        WebClient webClient = webClientBuilder
                .baseUrl(openAiConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        Duration timeout = openAiConfig.getTimeout();
        OpenAiResponse openAiResponse = webClient.post()
                .uri(RESPONSES_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiConfig.getApiKey())
                .bodyValue(buildRequestBody(requestAgent))
                .retrieve()
                .bodyToMono(OpenAiResponse.class)
                .timeout(timeout)
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebClientResponseException responseException) {
                        String responseBody = abbreviate(responseException.getResponseBodyAsString());
                        String message = "OpenAI 호출 실패: status=" + responseException.getStatusCode() + ", body=" + responseBody;
                        log.warn(message);
                        return Mono.error(new IllegalStateException(message, responseException));
                    }
                    log.warn("OpenAI 호출 실패: {}", throwable.getMessage());
                    return Mono.error(new IllegalStateException("OpenAI 호출 실패", throwable));
                })
                .block(timeout);

        if (openAiResponse == null) {
            throw new IllegalStateException("OpenAI 응답이 비어 있습니다.");
        }

        String jsonText = openAiResponse.firstText();
        if (!StringUtils.hasText(jsonText)) {
            throw new IllegalStateException("OpenAI 응답 본문이 없습니다.");
        }

        try {
            LlmAgentResult result = objectMapper.readValue(jsonText, LlmAgentResult.class);
            result.setResponseId(openAiResponse.getId());
            log.info("OpenAI 요청 성공: agentMode={}, hasSearchConditions={}", result.getAgentMode(), result.getSearchConditions() != null);
            return result;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("OpenAI 응답 파싱에 실패했습니다.", e);
        }
    }

    private Map<String, Object> buildRequestBody(RequestAgent requestAgent) {
        Map<String, Object> body = new HashMap<>();

        Map<String, Object> prompt = new HashMap<>();
        prompt.put("id", openAiConfig.getPromptId());
        Map<String, Object> variables = new HashMap<>();
        variables.put("now", LocalDateTime.now().toString());
        prompt.put("variables", variables);
        body.put("prompt", prompt);

        body.put("input", requestAgent.getUserMessage());

        if (StringUtils.hasText(requestAgent.getPreviousResponseId())) {
            body.put("previous_response_id", requestAgent.getPreviousResponseId());
        }
        return body;
    }

    @Getter
    @Setter
    private static class OpenAiResponse {
        private String id;
        private List<OpenAiOutput> output;

        String firstText() {
            if (output == null || output.isEmpty()) {
                return null;
            }
            OpenAiOutput firstOutput = output.getLast();
            if (firstOutput.getContent() == null || firstOutput.getContent().isEmpty()) {
                return null;
            }
            OpenAiContent firstContent = firstOutput.getContent().getFirst();
            return firstContent.getText();
        }
    }

    @Getter
    @Setter
    private static class OpenAiOutput {
        private List<OpenAiContent> content;
    }

    @Getter
    @Setter
    private static class OpenAiContent {
        private String type;
        private String text;
    }

    private String abbreviate(String text) {
        if (text == null) {
            return null;
        }
        if (text.length() <= 500) {
            return text;
        }
        return text.substring(0, 500) + "...(truncated)";
    }
}
