package com.kymokim.spirit.agent.service;

import com.kymokim.spirit.agent.dto.RequestAgent;
import com.kymokim.spirit.agent.dto.ResponseAgent;
import com.kymokim.spirit.agent.dto.SearchConditions;
import com.kymokim.spirit.agent.dto.AgentMode;
import com.kymokim.spirit.agent.dto.LlmAgentResult;
import com.kymokim.spirit.common.dto.ResponseLocationDto;
import com.kymokim.spirit.common.service.LocationService;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentService {

    private final OpenAiAgent openAiAgent;
    private final DefaultAgent defaultAgent;
    private final StoreRepository storeRepository;
    private final LocationService locationService;

    public ResponseAgent chatSearch(RequestAgent requestAgent) {
        Pageable pageable = PageRequest.of(0, 10);
        LlmAgentResult result = requestWithFallback(requestAgent);

        if (result.getAgentMode() == AgentMode.SHOW_RESULT) {
            SearchConditions searchConditions = result.getSearchConditions() != null ? result.getSearchConditions() : SearchConditions.builder().build();
            ResponseLocationDto.GetAddressDto address = null;
            
            if (Objects.equals(searchConditions.getLatitude(), null) || Objects.equals(searchConditions.getLongitude(), null)) {
                searchConditions.setLatitude(requestAgent.getLatitude());
                searchConditions.setLongitude(requestAgent.getLongitude());
            } else {
                address = locationService.getAddress(searchConditions.getLatitude(), searchConditions.getLongitude());
            }
            if (Objects.equals(searchConditions.getRadius(), null)) {
                searchConditions.setRadius(2.0);
            }
            result.setSearchConditions(searchConditions);

            Page<Store> storePage = storeRepository.findByMultipleCondition(
                    searchConditions.toLocationCriteria(),
                    searchConditions.getCategory() != null ? Set.of(searchConditions.getCategory()) : null,
                    searchConditions.getSearchKeyword(),
                    searchConditions.toFacilitiesCondition(),
                    searchConditions.getArrivalTime(),
                    searchConditions.getDrinkType(),
                    searchConditions.getMoods(),
                    searchConditions.getDrinkPriceOrder(),
                    pageable
            );

            if (storePage.isEmpty()) {
                result.setAgentMode(AgentMode.NO_RESULT);
                result.setAgentMessage("현재 위치에서는 조건에 맞는 가게를 찾지 못했어요.\n" +
                        "다른 위치나 조건을 알려주시면 다시 찾아볼게요.");

                return ResponseAgent.builder()
                        .llmAgentResult(result)
                        .changedAddress(address)
                        .stores(new PageImpl<>(Collections.emptyList(), pageable, 0))
                        .build();
            }

            Page<ResponseStore.SearchStoreDto> dtoPage = storePage.map(store ->
                    ResponseStore.SearchStoreDto.toDto(store, calculateRate(store), searchConditions.getDrinkType()));

            return ResponseAgent.builder()
                    .llmAgentResult(result)
                    .changedAddress(address)
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
