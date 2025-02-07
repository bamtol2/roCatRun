// domain/member/service/auth/JwtTokenService.java
package com.ssafy.roCatRun.domain.member.service.auth;

import com.ssafy.roCatRun.domain.member.dto.token.JwtTokens;
import com.ssafy.roCatRun.domain.member.repository.RefreshTokenRedisRepository;
import com.ssafy.roCatRun.global.exception.TokenRefreshException;
import com.ssafy.roCatRun.global.security.jwt.JwtTokenProvider;
import com.ssafy.roCatRun.global.security.jwt.JwtTokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JwtTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenGenerator jwtTokenGenerator;  // 추가
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    private static final long TOKEN_EXPIRATION_TIME_MS = 1000 * 60 * 60 * 24 * 14; // 14일

    public JwtTokens refreshToken(String refreshToken) {
        log.info("Received refresh token: {}", refreshToken);
        // 리프레시 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new TokenRefreshException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 토큰에서 사용자 ID 추출
        String userId = jwtTokenProvider.extractSubject(refreshToken);
        log.info("Extracted userId: {}", userId);

        // Redis에서 저장된 리프레시 토큰 조회
        String storedToken = refreshTokenRedisRepository.findByKey(userId)
                .orElseThrow(() -> new TokenRefreshException("저장된 리프레시 토큰을 찾을 수 없습니다."));
        log.info("Stored token from Redis: {}", storedToken);

        // 저장된 토큰과 요청된 토큰 비교
        if (!refreshToken.equals(storedToken)) {
            throw new TokenRefreshException("리프레시 토큰이 일치하지 않습니다.");
        }

        // JwtTokenGenerator를 사용하여 새로운 토큰 생성
        JwtTokens newTokens = jwtTokenGenerator.generate(userId);

        // Redis에 새로운 리프레시 토큰 저장
        refreshTokenRedisRepository.save(userId, newTokens.getRefreshToken(), TOKEN_EXPIRATION_TIME_MS);

        return newTokens;
    }
}