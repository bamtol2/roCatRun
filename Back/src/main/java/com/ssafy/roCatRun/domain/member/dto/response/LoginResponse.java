package com.ssafy.roCatRun.domain.member.dto.response;

import com.ssafy.roCatRun.domain.member.dto.token.AuthTokens;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private AuthTokens token; // JWT 토큰

    public LoginResponse(AuthTokens token) {
        this.token = token;
    }
}