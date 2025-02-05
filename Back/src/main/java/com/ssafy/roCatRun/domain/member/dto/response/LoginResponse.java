package com.ssafy.roCatRun.domain.member.dto.response;

import com.ssafy.roCatRun.domain.member.dto.token.JwtTokens;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private JwtTokens token; // JWT 토큰

    public LoginResponse(JwtTokens token) {
        this.token = token;
    }
}