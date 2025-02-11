package com.ssafy.roCatRun.domain.myPage.controller;

import com.ssafy.roCatRun.domain.myPage.dto.request.MyPageUpdateRequest;
import com.ssafy.roCatRun.domain.myPage.dto.response.MyPageResponse;
import com.ssafy.roCatRun.domain.myPage.service.MyPageService;
import com.ssafy.roCatRun.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 마이페이지 관리를 처리하는 컨트롤러
 * 회원 정보와 캐릭터 닉네임 조회, 수정 등의 기능 처리
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/domain/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 마이페이지 정보를 조회합니다.
     * @param authentication 현재 인증된 사용자 정보
     * @return 회원 정보와 캐릭터 닉네임
     * @throws IllegalStateException 인증 정보가 없는 경우
     */
    @GetMapping
    public ApiResponse<MyPageResponse> getMyPageInfo(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String memberId = authentication.getPrincipal().toString();
        log.debug("Fetching mypage info for member: {}", memberId);

        MyPageResponse response = myPageService.getMyPageInfo(Long.parseLong(memberId));
        return ApiResponse.success(response);
    }

    /**
     * 마이페이지 정보를 수정합니다.
     * @param authentication 현재 인증된 사용자 정보
     * @param request 수정할 정보 (닉네임, 키, 몸무게, 나이, 성별)
     * @return 수정 완료 응답
     * @throws IllegalStateException 인증 정보가 없는 경우
     */
    @PatchMapping
    public ApiResponse<Void> updateMyPageInfo(
            Authentication authentication,
            @Valid @RequestBody MyPageUpdateRequest request
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String memberId = authentication.getPrincipal().toString();
        log.debug("Updating mypage info for member: {}", memberId);

        myPageService.updateMyPageInfo(Long.parseLong(memberId), request);
        return ApiResponse.success(null);
    }
}
