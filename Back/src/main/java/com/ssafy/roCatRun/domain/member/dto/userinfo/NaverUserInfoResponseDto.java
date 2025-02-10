package com.ssafy.roCatRun.domain.member.dto.userinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverUserInfoResponseDto {
    private String resultcode;
    private String message;
    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        private String id;
        private String nickname;
        private String email;
        private String name;
        private String profile_image;
    }
}