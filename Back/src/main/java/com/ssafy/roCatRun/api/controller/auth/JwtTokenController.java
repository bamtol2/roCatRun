package com.ssafy.roCatRun.api.controller.auth;

import com.ssafy.roCatRun.domain.member.dto.token.JwtTokenRequest;
import com.ssafy.roCatRun.domain.member.dto.token.JwtTokenResponse;
import com.ssafy.roCatRun.domain.member.service.auth.JwtTokenService;
import com.ssafy.roCatRun.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
// hi
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "JWT 토큰 API", description = "JWT 토큰 관련 API")
public class JwtTokenController {

    private final JwtTokenService jwtTokenService;

    @PostMapping("/refresh/jwt")
    @Operation(summary = "JWT 토큰 재발급", description = "만료된 JWT 액세스 토큰을 리프레시 토큰으로 재발급합니다.")
    public ApiResponse<JwtTokenResponse> refreshToken(@RequestBody JwtTokenRequest request) {
        log.info("Refresh token request received: {}", request.getRefreshToken());
        return ApiResponse.success(JwtTokenResponse.from(jwtTokenService.refreshToken(request.getRefreshToken())));
    }
}