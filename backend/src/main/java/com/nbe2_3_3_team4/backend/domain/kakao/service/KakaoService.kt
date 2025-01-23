package com.nbe2_3_3_team4.backend.domain.kakao.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nbe2_3_3_team4.backend.domain.kakao.dto.KakaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.api.key}")
    private String KAKAO_API_KEY;

    public List<KakaoResponse.Place> searchPlace(String keyword, int size) {
        String kakaoLocalUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";

        // RestTemplate 초기화
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);

        // URI 수동으로 빌드
        // UriComponentsBuilder 를 사용하면 한글 인식 불가.. 자동으로 인코딩해서 그런듯..?
        String uri = kakaoLocalUrl + "?query=" + keyword + "&size=" + size;

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // REST API 호출
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            entity,
            JsonNode.class
        );

        JsonNode dataArray = Objects.requireNonNull(response.getBody()).get("documents");

        List<KakaoResponse.Place> result = new ArrayList<>();

        for (JsonNode data : dataArray) {
            if (data.get("address_name").asText().contains("서울")) {
                result.add(KakaoResponse.Place.from(data));
            }
        }

        return result;
    }

}
