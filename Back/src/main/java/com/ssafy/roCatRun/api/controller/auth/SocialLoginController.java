package com.ssafy.roCatRun.api.controller.auth;

import com.ssafy.roCatRun.domain.member.dto.token.JwtTokens;
import com.ssafy.roCatRun.domain.member.service.auth.GoogleService;
import com.ssafy.roCatRun.domain.member.service.auth.KakaoService;
import com.ssafy.roCatRun.domain.member.dto.response.LoginResponse;
import com.ssafy.roCatRun.domain.member.service.auth.NaverService;
import com.ssafy.roCatRun.global.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 소셜 로그인 콜백을 처리하는 컨트롤러
 * 프론트엔드에서 소셜 로그인 인증 후 받은 인가 코드를 처리
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SocialLoginController {

    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final GoogleService googleService;

    // 카카오 로그인 콜백 처리
    @GetMapping("/callback/kakao")
    public ApiResponse<LoginResponse> kakaoCallback(
            @RequestParam String code, // URL 파라미터로 전달되는 인가 코드
            HttpServletRequest request // 현재 도메인 정보를 얻기 위한 HTTP 요청 객체
    ) {
        String currentDomain = request.getServerName();
        return ApiResponse.success(kakaoService.kakaoLogin(code, currentDomain));
    }

    // 카카오 토큰 리프레시 처리
    @PostMapping("/refresh/kakao")
    public ApiResponse<JwtTokens> refreshKakaoToken(
            @RequestHeader("Refresh-Token") String refreshToken // HTTP 헤더에서 리프레시 토큰을 추출
    ) {
        return ApiResponse.success(kakaoService.refreshKakaoToken(refreshToken));
    }

    // 네이버 로그인 콜백 처리
    @GetMapping("/callback/naver")
    public ApiResponse<LoginResponse> naverCallback(
            @RequestParam String code,
            @RequestParam String state,
            HttpServletRequest request
    ) {
        String currentDomain = request.getServerName();
        return ApiResponse.success(naverService.naverLogin(code, state, currentDomain));
    }

    // 네이버 토큰 리프레시 처리
    @PostMapping("/refresh/naver")
    public ApiResponse<JwtTokens> refreshNaverToken(
            @RequestHeader("Refresh-Token") String refreshToken
    ) {
        return ApiResponse.success(naverService.refreshNaverToken(refreshToken));
    }

    // 구글 로그인 콜백 처리
    @GetMapping("/callback/google")
    public ApiResponse<LoginResponse> googleCallback(
            @RequestParam String code,
            HttpServletRequest request
    ) {
        String currentDomain = request.getServerName();

        log.info("Google OAuth Callback - Authorization Code: {}", code);
        log.info("Current Domain: {}", currentDomain);

        try {
            LoginResponse response = googleService.googleLogin(code, currentDomain);
            log.info("Google Login Success - User Info: {}", response);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("Google Login Error", e);
            throw e;
        }
    }

    // 구글 토큰 리프레시 처리
    @PostMapping("/refresh/google")
    public ApiResponse<JwtTokens> refreshGoogleToken(
            @RequestHeader("Refresh-Token") String refreshToken
    ) {
        return ApiResponse.success(googleService.refreshGoogleToken(refreshToken));
    }
}