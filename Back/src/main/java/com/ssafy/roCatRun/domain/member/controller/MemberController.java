package com.ssafy.roCatRun.domain.member.controller;

import com.ssafy.roCatRun.domain.member.service.MemberService;
import com.ssafy.roCatRun.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 정보 관리를 처리하는 컨트롤러
 * 회원 정보 조회, 수정, 탈퇴 등의 기능 처리
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "회원 API", description = "회원 정보 관리 관련 API")
public class MemberController {

    private final MemberService memberService;

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "회원 정보와 연관된 모든 데이터를 삭제합니다.")
    public ApiResponse<Void> deleteMember(
            @AuthenticationPrincipal String memberId
    ) {
        memberService.deleteMember(Long.parseLong(memberId));
        return ApiResponse.success(null);
    }
}