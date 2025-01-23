package com.nbe2_3_3_team4.backend.security.dto;

import io.swagger.v3.oas.annotations.Parameter;

public record TokenRequest (

) {
    public record Update(
        @Parameter(description = "refresh token")
        String refreshToken
    ) {

    }
}
