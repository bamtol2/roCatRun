package com.ssafy.roCatRun.domain.member.dto.token;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokens {
    private String accessToken; // JWT 액세스 토큰
    private String refreshToken; // JWT 리프레시 토큰
    private String grantType; // 토큰 타입
    private Long expiresIn; // 토큰 만료 시간

    public static AuthTokens of(String accessToken, String refreshToken, String grantType, Long expiresIn) {
        return new AuthTokens(accessToken, refreshToken, grantType, expiresIn);
    }
}
