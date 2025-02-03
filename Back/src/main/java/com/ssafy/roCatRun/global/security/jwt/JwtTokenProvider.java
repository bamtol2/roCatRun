package com.ssafy.roCatRun.global.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SecretKey key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Jwts.SIG.HS512.key().build();  // 안전한 키 생성
    }

    public String accessTokenGenerate(String subject, Date expiredAt) {
        return Jwts.builder()
                .subject(subject)
                .expiration(expiredAt)
                .signWith(key)
                .compact();
    }

    public String refreshTokenGenerate(String subject, Date expiredAt) {
        return Jwts.builder()
                .subject(subject)    // subject 추가
                .expiration(expiredAt)
                .signWith(key)
                .compact();
    }

    // JWT 토큰 유효성 검증 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            logger.error("JWT 토큰이 유효하지 않습니다: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 사용자 ID 추출
    public String getSubject(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // extractSubject는 getSubject를 호출하도록 변경
    public String extractSubject(String token) {
        try {
            return getSubject(token);
        } catch (Exception e) {
            throw new RuntimeException("토큰에서 사용자 정보를 추출하는데 실패했습니다.", e);
        }
    }
}