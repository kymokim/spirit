package com.kymokim.spirit.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kymokim.spirit.common.dto.ResponseLocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:/secret/api-key.properties")
public class LocationService {

    @Value("${kakao.rest.key}")
    private String kakaoRestKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseLocationDto.GetAddressDto getAddress(double latitude, double longitude) {
        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=" + longitude + "&y=" + latitude;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String address = null;
        try {
            JsonNode root = new ObjectMapper().readTree(response.getBody());
            if (!root.get("documents").isEmpty()) {
                JsonNode document = root.get("documents").get(0);

                if (document.has("road_address") && !document.get("road_address").isNull()) {
                    address = document.get("road_address").get("address_name").asText();
                } else {
                    address = document.get("address").get("address_name").asText(); // 지번주소 fallback
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseLocationDto.GetAddressDto.toDto(address);
    }

    public ResponseLocationDto.GetCoordinateDto getCoordinate(String address) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        Double latitude = 0D;
        Double longitude = 0D;
        try {
            JsonNode root = new ObjectMapper().readTree(response.getBody());
            if (!root.get("documents").isEmpty()) {
                JsonNode addressNode = root.get("documents").get(0).get("address");
                latitude = addressNode.get("y").asDouble();
                longitude = addressNode.get("x").asDouble();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseLocationDto.GetCoordinateDto.toDto(latitude, longitude);
    }

    public ResponseLocationDto.GetRoadAddressAndCoordinateDto getRoadAddressAndCoordinate(double latitude, double longitude) {

        ResponseLocationDto.GetAddressDto getAddressDto = getAddress(latitude, longitude);
        ResponseLocationDto.GetCoordinateDto getCoordinateDto = getCoordinate(getAddressDto.getAddress());

        return ResponseLocationDto.GetRoadAddressAndCoordinateDto.toDto(getAddressDto, getCoordinateDto);
    }
}
