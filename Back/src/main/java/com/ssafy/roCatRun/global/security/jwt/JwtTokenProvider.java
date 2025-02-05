package com.ssafy.roCatRun.global.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;

/**
 * JWT 토큰의 생성, 검증, 정보 추출 등 핵심 기능을 제공하는 클래스
 * 실제 JWT 토큰 조작과 관련된 모든 저수준 작업을 처리
 */
@Slf4j
@Component
public class JwtTokenProvider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SecretKey key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Jwts.SIG.HS512.key().build();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        String userId = extractSubject(token);
        UserDetails userDetails = new User(userId, "", new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
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
                .subject(subject)
                .expiration(expiredAt)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            log.info("Validating token: {}", token);
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT 토큰이 유효하지 않습니다: {}", e.getMessage());
            return false;
        }
    }

    public String extractSubject(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            throw new RuntimeException("토큰에서 사용자 정보를 추출하는데 실패했습니다.", e);
        }
    }
}