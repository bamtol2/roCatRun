package com.ssafy.roCatRun.api.controller.auth;

import com.ssafy.roCatRun.domain.member.dto.token.JwtTokenRequest;
import com.ssafy.roCatRun.domain.member.dto.token.JwtTokenResponse;
import com.ssafy.roCatRun.domain.member.service.auth.JwtTokenService;
import com.ssafy.roCatRun.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController // @Controller + @ResponseBody의 조합으로, JSON 형태로 객체 데이터를 반환
@RequiredArgsConstructor
@RequestMapping("/api/auth")

public class JwtTokenController {

    private final JwtTokenService jwtTokenService;

    @PostMapping("/refresh/jwt")
    public ApiResponse<JwtTokenResponse> refreshToken(@RequestBody JwtTokenRequest request) { // HTTP 요청 본문을 JwtTokenRequest 객체로 변환
        log.info("Refresh token request received: {}", request.getRefreshToken());
        // 1. request에서 refreshToken을 추출
        // 2. jwtTokenService.refreshToken()을 호출하여 새로운 토큰 발급
        // 3. 발급된 토큰을 JwtTokenResponse 객체로 변환
        // 4. ApiResponse로 감싸서 반환 (global/common/ApiResponse.java 사용)
        return ApiResponse.success(JwtTokenResponse.from(jwtTokenService.refreshToken(request.getRefreshToken())));
    }
}