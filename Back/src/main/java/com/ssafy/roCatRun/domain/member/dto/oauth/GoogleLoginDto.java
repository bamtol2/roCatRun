package com.ssafy.roCatRun.domain.member.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleLoginDto {

    @Getter
    @NoArgsConstructor
    public static class TokenResponse {
        private String access_token;
        private String expires_in;
        private String refresh_token;
        private String scope;
        private String token_type;
        private String id_token;
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