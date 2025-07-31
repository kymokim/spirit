package com.kymokim.spirit.main.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.main.store.dto.BusinessInfo;
import com.kymokim.spirit.main.store.dto.BusinessResponse;
import com.kymokim.spirit.main.store.exception.StoreErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:/secret/api-key.properties")
public class BusinessRegistrationValidator {

    private final Logger LOGGER = LoggerFactory.getLogger(BusinessRegistrationValidator.class);

    @Value("${odcloud.decodingKey}")
    private String decodingKey;

    @Transactional
    public Boolean validateBusiness(String businessRegistrationNumber, String representativeName, String openingDate){
        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl("https://api.odcloud.kr/api/nts-businessman/v1")
                .build();
        try {
            BusinessResponse response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/validate")
                            .queryParam("serviceKey", decodingKey)
                            .build())
                    .bodyValue(convertValidateRequestBody(businessRegistrationNumber, representativeName, openingDate))
                    .retrieve()
                    .bodyToMono(BusinessResponse.class)
                    .block();
            LOGGER.info(businessRegistrationNumber + " 사업자 진위 응답 데이터: " + response.getStatus_code());

            return response.isValid();

        } catch (Exception e) {
            LOGGER.info("사업자등록증 진위여부 확인 API 에러 : " + e.getMessage());
            throw new CustomException(StoreErrorCode.BUSINESS_REGISTRATION_VALIDATE_FAILED);
        }
    }

    private String convertValidateRequestBody(String businessNumber, String representativeName, String openingDate){
        try {
            Map<String, Object> body = new HashMap<>();
            List<BusinessInfo> businesses = new ArrayList<>();
            businesses.add(new BusinessInfo(businessNumber, representativeName, openingDate));
            body.put("businesses", businesses);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(body);
        }catch (Exception e){
            throw new CustomException(StoreErrorCode.BUSINESS_REGISTRATION_VALIDATE_FAILED, e.getMessage());
        }
    }
}
