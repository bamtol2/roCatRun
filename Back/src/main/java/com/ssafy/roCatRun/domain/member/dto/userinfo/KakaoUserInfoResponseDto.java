package com.ssafy.roCatRun.domain.member.dto.userinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoResponseDto {
    private Long id;
    private Properties properties;
    private KakaoAccount kakao_account;

    @Getter
    @NoArgsConstructor
    public static class Properties {
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private String email;
    }
}