// global/security/jwt/JwtTokenGenerator.java
package com.ssafy.roCatRun.global.security.jwt;

import com.ssafy.roCatRun.domain.member.dto.token.JwtTokens;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenGenerator {
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;     // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14;  // 14일

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokens generate(String userId) {
        long now = (new Date()).getTime();
        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        String accessToken = jwtTokenProvider.accessTokenGenerate(userId, accessTokenExpiredAt);
        String refreshToken = jwtTokenProvider.refreshTokenGenerate(userId, refreshTokenExpiredAt);

        return JwtTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}