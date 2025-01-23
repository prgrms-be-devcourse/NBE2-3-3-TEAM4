package com.nbe2_3_3_team4.backend.security.dto

class TokenResponse {
    @JvmRecord
    data class Create(val accessToken: String,
                      val refreshToken: String,
                      val email: String
    ) {
        companion object {
            fun from(accessToken: String, refreshToken: String, email: String): Create {
                return Create(accessToken, refreshToken, email)
            }
        }
    }
}
