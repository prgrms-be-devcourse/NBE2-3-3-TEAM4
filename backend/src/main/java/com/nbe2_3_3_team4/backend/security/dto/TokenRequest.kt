package com.nbe2_3_3_team4.backend.security.dto

import io.swagger.v3.oas.annotations.Parameter

class TokenRequest {
    data class Update(@field:Parameter(description = "refresh token") @param:Parameter(description = "refresh token") val refreshToken: String
    )
}
