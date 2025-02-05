// domain/member/dto/token/JwtTokens.java
package com.ssafy.roCatRun.domain.member.dto.token;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokens {
    private String accessToken;
    private String refreshToken;
}