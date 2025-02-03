package com.ssafy.roCatRun.domain.member.dto.response;

import com.ssafy.roCatRun.domain.member.dto.token.AuthTokens;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 로그인 성공 후 프론트에 전달하는 최종 응답 DTO

@Getter
@NoArgsConstructor
public class LoginResponse {
    private Long id; // 우리 서비스의 사용자 ID
    private String nickname; // 우리 서비스의 닉네임
    private String email; // 이메일
    private AuthTokens token; // 우리 서비스의 JWT 토큰

    public LoginResponse(Long id, String nickname, String email, AuthTokens token) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.token = token;
    }
}
