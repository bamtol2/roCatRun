package com.ssafy.roCatRun.api.controller.auth;

import com.ssafy.roCatRun.domain.member.dto.token.AuthTokens;
import com.ssafy.roCatRun.domain.member.service.auth.KakaoService;
//import com.ssafy.roCatRun.domain.member.service.auth.NaverService;
//import com.ssafy.roCatRun.domain.member.service.auth.GoogleService;
import com.ssafy.roCatRun.domain.member.dto.response.LoginResponse;
import com.ssafy.roCatRun.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 소셜 로그인 콜백을 처리하는 컨트롤러
 * 프론트엔드에서 소셜 로그인 인증 후 받은 인가 코드를 처리
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "소셜 로그인 API", description = "소셜 로그인 관련 API")
public class SocialLoginController {

    private final KakaoService kakaoService;
//    private final NaverService naverService;
//    private final GoogleService googleService;

    /**
     * 카카오 로그인 콜백 처리
     * @param code 카카오로부터 받은 인가 코드
     * @param request 현재 도메인 정보를 얻기 위한 HTTP 요청 객체
     * @return 로그인 응답 (회원 정보와 토큰)
     */

    @GetMapping("/callback/kakao")
    @Operation(summary = "카카오 로그인 콜백", description = "카카오 소셜 로그인 콜백을 처리합니다.")
    public ApiResponse<LoginResponse> kakaoCallback(
            @RequestParam String code, // code 카카오로부터 받은 인가 코드
            HttpServletRequest request // 현재 도메인 정보를 얻기 위한 HTTP 요청 객체
    ) {
        String currentDomain = request.getServerName();
        return ApiResponse.success(kakaoService.kakaoLogin(code, currentDomain));
    }

    /**
     * 카카오 토큰 재발급 처리
     * @param refreshToken 재발급에 사용할 리프레시 토큰
     * @return 새로 발급된 토큰 정보
     */
    @PostMapping("/refresh/kakao")
    @Operation(summary = "카카오 토큰 재발급", description = "만료된 액세스 토큰을 리프레시 토큰으로 재발급합니다.")
    public ApiResponse<AuthTokens> refreshKakaoToken(
            @RequestHeader("Refresh-Token") String refreshToken
    ) {
        return ApiResponse.success(kakaoService.refreshKakaoToken(refreshToken));
    }

//    @GetMapping("/callback/naver")
//    @Operation(summary = "네이버 로그인 콜백", description = "네이버 소셜 로그인 콜백을 처리합니다.")
//    public ApiResponse<LoginResponse> naverCallback(
//            @RequestParam String code,
//            @RequestParam String state,
//            HttpServletRequest request
//    ) {
//        String currentDomain = request.getServerName();
//        return ApiResponse.success(naverService.naverLogin(code, state, currentDomain));
//    }
//
//    @GetMapping("/callback/google")
//    @Operation(summary = "구글 로그인 콜백", description = "구글 소셜 로그인 콜백을 처리합니다.")
//    public ApiResponse<LoginResponse> googleCallback(
//            @RequestParam String code,
//            HttpServletRequest request
//    ) {
//        String currentDomain = request.getServerName();
//        return ApiResponse.success(googleService.googleLogin(code, currentDomain));
//    }
}