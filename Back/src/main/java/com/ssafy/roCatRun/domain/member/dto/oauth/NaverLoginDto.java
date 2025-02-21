package com.ssafy.roCatRun.domain.member.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverLoginDto {

    @Getter
    @NoArgsConstructor
    public static class TokenResponse {
        private String access_token;
        private String refresh_token;
        private String token_type;
        private String expires_in;
        private String error;
        private String error_description;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenRequest {
        private String grant_type;
        private String client_id;
        private String client_secret;
        private String refresh_token;
    }
}