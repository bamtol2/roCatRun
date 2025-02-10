package com.ssafy.roCatRun.domain.member.dto.token;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenResponse {
    private TokenInfo token;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
    }

    public static JwtTokenResponse from(JwtTokens tokens) {
        return JwtTokenResponse.builder()
                .token(TokenInfo.builder()
                        .accessToken(tokens.getAccessToken())
                        .refreshToken(tokens.getRefreshToken())
                        .build())
                .build();
    }
}