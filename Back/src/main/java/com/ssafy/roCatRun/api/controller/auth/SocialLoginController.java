package com.ssafy.roCatRun.api.controller.auth;

import com.ssafy.roCatRun.domain.member.service.auth.SocialLoginService;
import com.ssafy.roCatRun.global.common.ApiResponse;
import com.ssafy.roCatRun.domain.member.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "소셜 로그인 API", description = "소셜 로그인 관련 API")
public class SocialLoginController {

    private final SocialLoginService socialLoginService;

    @GetMapping("/callback/kakao")
    @Operation(summary = "카카오 로그인 콜백", description = "카카오 소셜 로그인 콜백을 처리합니다.")
    public ApiResponse<LoginResponse> kakaoCallback(
            @RequestParam String code,
            HttpServletRequest request
    ) {
        String currentDomain = request.getServerName();
        return ApiResponse.success(socialLoginService.kakaoLogin(code, currentDomain));
    }

    @GetMapping("/callback/naver")
    @Operation(summary = "네이버 로그인 콜백", description = "네이버 소셜 로그인 콜백을 처리합니다.")
    public ApiResponse<LoginResponse> naverCallback(
            @RequestParam String code,
            @RequestParam String state,
            HttpServletRequest request
    ) {
        String currentDomain = request.getServerName();
        return ApiResponse.success(socialLoginService.naverLogin(code, state, currentDomain));
    }

    @GetMapping("/callback/google")
    @Operation(summary = "구글 로그인 콜백", description = "구글 소셜 로그인 콜백을 처리합니다.")
    public ApiResponse<LoginResponse> googleCallback(
            @RequestParam String code,
            HttpServletRequest request
    ) {
        String currentDomain = request.getServerName();
        return ApiResponse.success(socialLoginService.googleLogin(code, currentDomain));
    }
}