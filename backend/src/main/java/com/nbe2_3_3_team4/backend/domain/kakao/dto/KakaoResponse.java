package com.nbe2_3_3_team4.backend.domain.kakao.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record KakaoResponse() {

    public record Place(
        Long placeId,
        String address,
        String roadAddress,
        Double x,
        Double y,
        String name
    ) {

        public static Place from(JsonNode data) {
            return new Place(
                data.get("id").asLong(),
                data.get("address_name").asText(),
                data.get("road_address_name").asText(),
                data.get("x").asDouble(),
                data.get("y").asDouble(),
                data.get("place_name").asText()
            );
        }

    }

}
