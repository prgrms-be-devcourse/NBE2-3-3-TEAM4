package com.nbe2_3_3_team4.backend.security.dto;

public record TokenResponse() {

    public record Create(
        String accessToken,
        String refreshToken,
        String email
    ) {

        public static Create from(String accessToken, String refreshToken, String email) {
            return new Create(accessToken, refreshToken, email);
        }

    }


}
