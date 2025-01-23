package com.nbe2_3_3_team4.backend.domain.kakao.service

import com.fasterxml.jackson.databind.JsonNode
import com.nbe2_3_3_team4.backend.domain.kakao.dto.KakaoResponse.Place
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class KakaoService {
    @Value("\${kakao.api.key}")
    private val KAKAO_API_KEY: String? = null

    fun searchPlace(keyword: String, size: Int): List<Place> {
        val kakaoLocalUrl = "https://dapi.kakao.com/v2/local/search/keyword.json"

        // RestTemplate 초기화
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers["Authorization"] = "KakaoAK $KAKAO_API_KEY"

        // URI 수동으로 빌드
        // UriComponentsBuilder 를 사용하면 한글 인식 불가.. 자동으로 인코딩해서 그런듯..?
        val uri = "$kakaoLocalUrl?query=$keyword&size=$size"

        val entity: HttpEntity<*> = HttpEntity<Any>(headers)

        // REST API 호출
        val response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                JsonNode::class.java
        )

        val dataArray = response.body?.get("documents")

        val result: MutableList<Place> = ArrayList()

        if (dataArray != null) {
            for (data in dataArray) {
                if (data["address_name"].asText().contains("서울")) {
                    result.add(Place.from(data))
                }
            }
        }

        return result
    }
}
