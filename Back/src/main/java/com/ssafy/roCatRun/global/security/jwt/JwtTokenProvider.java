package com.ssafy.roCatRun.global.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey key;

    /**
     * 생성자에서 JWT 서명에 사용할 키를 초기화합니다.
     * @param secretKey application.yml에서 설정한 JWT 비밀키
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Jwts.SIG.HS512.key().build();
    }

    /**
     * HTTP 요청 헤더에서 Bearer 토큰을 추출합니다.
     * @param request HTTP 요청
     * @return 추출된 JWT 토큰 (Bearer 제거)
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * JWT 토큰으로부터 인증 정보를 생성합니다.
     * @param token JWT 토큰
     * @return Spring Security 인증 객체
     */
    public Authentication getAuthentication(String token) {
        String userId = extractSubject(token);
        UserDetails userDetails = new User(userId, "", new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(userId, "", userDetails.getAuthorities());
    }

    /**
     * 액세스 토큰을 생성합니다.
     * @param subject 토큰에 담을 사용자 식별자
     * @param expiredAt 만료 시간
     * @return 생성된 JWT 액세스 토큰
     */
    public String accessTokenGenerate(String subject, Date expiredAt) {
        return Jwts.builder()
                .subject(subject)
                .expiration(expiredAt)
                .signWith(key)
                .compact();
    }

    /**
     * 리프레시 토큰을 생성합니다.
     * @param subject 토큰에 담을 사용자 식별자
     * @param expiredAt 만료 시간
     * @return 생성된 JWT 리프레시 토큰
     */
    public String refreshTokenGenerate(String subject, Date expiredAt) {
        return Jwts.builder()
                .subject(subject)
                .expiration(expiredAt)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     * @param token 검증할 JWT 토큰
     * @return 토큰 유효성 여부
     */
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

    /**
     * JWT 토큰에서 subject(사용자 식별자)를 추출합니다.
     * @param token JWT 토큰
     * @return 추출된 사용자 식별자
     */
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