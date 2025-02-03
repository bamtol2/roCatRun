package com.ssafy.roCatRun.domain.member.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    private String refreshToken;
    private String memberId;
    private Long expirationTime;
}