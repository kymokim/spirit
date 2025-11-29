package com.kymokim.spirit.agent.service;

import com.kymokim.spirit.agent.dto.RequestAgent;
import com.kymokim.spirit.agent.dto.ResponseAgent;
import com.kymokim.spirit.agent.dto.SearchConditions;
import com.kymokim.spirit.agent.dto.AgentMode;
import com.kymokim.spirit.agent.dto.LlmAgentResult;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentService {

    private final OpenAiAgent openAiAgent;
    private final DefaultAgent defaultAgent;
    private final StoreRepository storeRepository;

    public ResponseAgent chatSearch(RequestAgent requestAgent) {
        Pageable pageable = PageRequest.of(0, 10);
        LlmAgentResult result = requestWithFallback(requestAgent);

        if (result.getAgentMode() == AgentMode.SHOW_RESULT) {
            SearchConditions searchConditions = result.getSearchConditions() != null ? result.getSearchConditions() : SearchConditions.builder().build();

            if (Objects.equals(searchConditions.getLatitude(), null) || Objects.equals(searchConditions.getLongitude(), null)) {
                searchConditions.setLatitude(requestAgent.getLatitude());
                searchConditions.setLongitude(requestAgent.getLongitude());
            }
            if (Objects.equals(searchConditions.getRadius(), null)) {
                searchConditions.setRadius(2.0);
            }
            result.setSearchConditions(searchConditions);

            Page<Store> storePage = storeRepository.findByMultipleCondition(
                    searchConditions.toLocationCriteria(),
                    searchConditions.getCategory() != null ? searchConditions.getCategory().name() : null,
                    searchConditions.getSearchKeyword(),
                    searchConditions.toFacilitiesCondition(),
                    searchConditions.getArrivalTime(),
                    searchConditions.getDrinkType(),
                    searchConditions.getMoods(),
                    searchConditions.getDrinkPriceOrder(),
                    pageable
            );

            Page<ResponseStore.SearchStoreDto> dtoPage = storePage.map(store ->
                    ResponseStore.SearchStoreDto.toDto(store, calculateRate(store), searchConditions.getDrinkType()));

            return ResponseAgent.builder()
                    .llmAgentResult(result)
                    .stores(dtoPage)
                    .build();
        }

        return ResponseAgent.builder()
                .llmAgentResult(result)
                .stores(new PageImpl<>(Collections.emptyList(), pageable, 0))
                .build();
    }

    private LlmAgentResult requestWithFallback(RequestAgent requestAgent) {
        try {
            return openAiAgent.request(requestAgent);
        } catch (Exception exception) {
            log.warn("OpenAI 에이전트 실패, 기본 에이전트로 대체합니다.", exception);
            return defaultAgent.request(requestAgent);
        }
    }

    private double calculateRate(Store store) {
        if (store.getReviewCount() == null || store.getReviewCount() == 0) {
            return 0;
        }
        double rateAvg = store.getTotalRate() / store.getReviewCount();
        return Math.round(rateAvg * 100.0) / 100.0;
    }
}
