package com.nbe2_3_3_team4.backend.domain.kakao.dto

import com.fasterxml.jackson.databind.JsonNode

class KakaoResponse {
    data class Place(val placeId: Long,
                     val address: String,
                     val roadAddress: String,
                     val x: Double,
                     val y: Double,
                     val name: String
    ) {
        companion object {
            fun from(data: JsonNode): Place {
                return Place(
                        data["id"].asLong(),
                        data["address_name"].asText(),
                        data["road_address_name"].asText(),
                        data["x"].asDouble(),
                        data["y"].asDouble(),
                        data["place_name"].asText()
                )
            }
        }
    }
}
