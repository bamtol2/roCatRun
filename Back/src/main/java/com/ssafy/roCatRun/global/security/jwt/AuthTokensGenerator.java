package com.ssafy.roCatRun.global.security.jwt;

import com.ssafy.roCatRun.domain.member.dto.token.AuthTokens;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.Date;

/**
 * JWT 토큰 생성을 담당하는 유틸리티 클래스
 * Access Token과 Refresh Token을 생성하고 관리
 */
@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {
    // 토큰 타입 설정
    private static final String BEARER_TYPE = "Bearer";

    // 토큰 만료 시간 설정
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;     // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14;  // 14일

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 사용자 ID를 기반으로 Access Token과 Refresh Token을 생성
     *
     * @param uid 사용자 식별자
     * @return AuthTokens 생성된 액세스 토큰과 리프레시 토큰을 포함한 객체
     */
    public AuthTokens generate(String uid) {
        // 현재 시간 기준으로 토큰 만료 시간 설정
        long now = (new Date()).getTime();
        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        // JwtTokenProvider를 통해 실제 토큰 생성
        String accessToken = jwtTokenProvider.accessTokenGenerate(uid, accessTokenExpiredAt);
        String refreshToken = jwtTokenProvider.refreshTokenGenerate(uid, refreshTokenExpiredAt);

        // 토큰 정보를 담은 AuthTokens 객체 반환
        return AuthTokens.of(
                accessToken,          // 액세스 토큰
                refreshToken,         // 리프레시 토큰
                BEARER_TYPE,          // 토큰 타입 (Bearer)
                ACCESS_TOKEN_EXPIRE_TIME / 1000L  // 만료 시간(초 단위로 변환)
        );
    }
}