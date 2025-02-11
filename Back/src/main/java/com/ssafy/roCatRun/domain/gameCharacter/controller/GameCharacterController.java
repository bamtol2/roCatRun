package com.ssafy.roCatRun.domain.gameCharacter.controller;

import com.ssafy.roCatRun.domain.gameCharacter.dto.request.GameCharacterCreateRequest;
import com.ssafy.roCatRun.domain.gameCharacter.dto.request.NicknameUpdateRequest;
import com.ssafy.roCatRun.domain.gameCharacter.dto.response.GameCharacterResponse;
import com.ssafy.roCatRun.domain.gameCharacter.service.GameCharacterService;
import org.springframework.security.core.userdetails.UserDetails;
import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.ssafy.roCatRun.domain.gameCharacter.dto.response.RankingListResponse;
import com.ssafy.roCatRun.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 캐릭터 관리를 처리하는 컨트롤러
 * 캐릭터 생성, 조회, 수정 등의 기능 처리
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/domain/characters")
public class GameCharacterController {

    private final GameCharacterService gameCharacterService;

    /**
     * 새로운 캐릭터를 생성합니다.
     * @param request 캐릭터 생성 요청 정보 (닉네임, 키, 몸무게, 나이, 성별)
     * @param authentication 현재 인증된 사용자 정보
     * @return 생성된 캐릭터 정보
     * @throws IllegalStateException 인증 정보가 없는 경우
     */
    @PostMapping
    public ApiResponse<GameCharacterResponse> createCharacter(
            @Valid @RequestBody GameCharacterCreateRequest request,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String memberId = authentication.getPrincipal().toString();
        log.debug("Creating character for member: {}", memberId);

        GameCharacter gameCharacter = gameCharacterService.createCharacter(request, Long.parseLong(memberId));
        return ApiResponse.success(new GameCharacterResponse(gameCharacter));
    }

    /**
     * 캐릭터 닉네임의 중복 여부를 확인합니다.
     * @param nickname 중복 확인할 닉네임
     * @return 중복 여부 (true: 중복, false: 사용 가능)
     */
    @GetMapping("/check-nickname/{nickname}")
    public ApiResponse<Boolean> checkNicknameDuplicate(@PathVariable String nickname) {
        boolean isDuplicate = gameCharacterService.checkNicknameDuplicate(nickname);
        return isDuplicate
                ? ApiResponse.success("중복된 닉네임이 있습니다.", true)
                : ApiResponse.success("사용 가능한 닉네임입니다.", false);
    }

    /**
     * 현재 로그인한 회원의 캐릭터 정보를 조회합니다.
     * @param authentication 현재 인증된 사용자 정보
     * @return 캐릭터 정보
     * @throws IllegalStateException 인증 정보가 없는 경우
     */
    @GetMapping("/me")
    public ApiResponse<GameCharacterResponse> getMyCharacter(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String memberId = authentication.getPrincipal().toString();
        log.debug("Fetching character for member: {}", memberId);

        GameCharacter gameCharacter = gameCharacterService.getCharacterByMemberId(Long.parseLong(memberId));
        return ApiResponse.success(new GameCharacterResponse(gameCharacter));
    }

    /**
     * 캐릭터의 닉네임을 수정합니다.
     * @param authentication 현재 인증된 사용자 정보
     * @param request 닉네임 수정 요청 정보
     * @return 수정 완료 응답
     * @throws IllegalStateException 인증 정보가 없는 경우
     */
    @PatchMapping("/nickname")
    public ApiResponse<Void> updateNickname(
            Authentication authentication,
            @Valid @RequestBody NicknameUpdateRequest request
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String memberId = authentication.getPrincipal().toString();
        log.debug("Updating nickname for member: {}, new nickname: {}", memberId, request.getNewNickname());

        gameCharacterService.updateNickname(Long.parseLong(memberId), request.getNewNickname());
        return ApiResponse.success(null);
    }

    @GetMapping("/rankings")
    public ApiResponse<RankingListResponse> getRankings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        RankingListResponse response = gameCharacterService.getRankings(memberId, page, size);
        return ApiResponse.success(response);
    }
}