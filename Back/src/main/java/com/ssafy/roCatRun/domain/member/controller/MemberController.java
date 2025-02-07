package com.ssafy.roCatRun.domain.member.controller;

import com.ssafy.roCatRun.domain.member.dto.request.MemberProfileUpdateRequest;
import com.ssafy.roCatRun.domain.member.service.MemberService;
import com.ssafy.roCatRun.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 정보 관리를 처리하는 컨트롤러
 * 회원 정보 조회, 수정, 탈퇴, 로그아웃 등의 기능 처리
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/domain/members")  // domain -> api로 변경
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 탈퇴
     * 회원의 모든 데이터 삭제 (Character, RefreshToken 등)
     */
    @DeleteMapping("/me")
    public ApiResponse<Void> deleteMember(
            @AuthenticationPrincipal String memberId
    ) {
        memberService.deleteMember(Long.parseLong(memberId));
        return ApiResponse.success(null);
    }

    /**
     * 로그아웃
     * Redis에 저장된 RefreshToken 삭제
     */
    @PostMapping("/logout")  // 로그아웃 추가
    public ApiResponse<Void> logout(
            @AuthenticationPrincipal String memberId
    ) {
        memberService.logout(Long.parseLong(memberId));
        return ApiResponse.success(null);
    }

    /**
     * 회원 프로필 정보 수정
     * 키, 몸무게, 나이, 성별 정보 업데이트
     */
    @PutMapping("/profile")
    public ApiResponse<Void> updateMemberProfile(
            @AuthenticationPrincipal String memberId,
            @RequestBody MemberProfileUpdateRequest request
    ) {
        memberService.updateMemberProfile(Long.parseLong(memberId), request);
        return ApiResponse.success(null);
    }
}