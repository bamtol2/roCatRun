package com.ssafy.roCatRun.global.security.jwt;

// jwt 관련 로직을 처리하는 컴포넌트
// accessToken 과 refreshToken 을 생성
// accessToken에서 uid를 추출하는 함수, 유효한 토큰인지 확인하는 함수 등을 추가

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.logging.Logger;

@Component
public class JwtTokenProvider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Key key;

    public JwtTokenProvider(@Value("${custom.jwt.secretKey}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String accessTokenGenerate(String subject, Date expiredAt) {
        return Jwts.builder()
                .setSubject(subject)	//uid
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    public String refreshTokenGenerate(Date expiredAt) {
        return Jwts.builder()
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
