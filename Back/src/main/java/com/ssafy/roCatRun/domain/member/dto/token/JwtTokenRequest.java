package com.ssafy.roCatRun.domain.member.dto.token;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtTokenRequest {
    private String accessToken;
    private String refreshToken;
}