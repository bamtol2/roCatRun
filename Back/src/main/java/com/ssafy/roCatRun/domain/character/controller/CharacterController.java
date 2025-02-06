package com.ssafy.roCatRun.domain.character.controller;

import com.ssafy.roCatRun.domain.character.dto.request.CharacterCreateRequest;
import com.ssafy.roCatRun.domain.character.dto.response.CharacterResponse;
import com.ssafy.roCatRun.domain.character.service.CharacterService;
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
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterService characterService;

    /**
     * 새로운 캐릭터를 생성합니다.
     * @param request 캐릭터 생성 요청 정보 (닉네임)
     * @param authentication 현재 인증된 사용자 정보
     * @return 생성된 캐릭터 정보
     */
    @PostMapping
    public ApiResponse<CharacterResponse> createCharacter(
            @Valid @RequestBody CharacterCreateRequest request,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String memberId = authentication.getPrincipal().toString();
        log.debug("Creating character for member: {}", memberId);

        com.ssafy.roCatRun.domain.character.entity.Character character =
                characterService.createCharacter(request, Long.parseLong(memberId));
        return ApiResponse.success(new CharacterResponse(character));
    }

    /**
     * 캐릭터 닉네임의 중복 여부를 확인합니다.
     * @param nickname 중복 확인할 닉네임
     * @return 중복 여부 (true: 중복, false: 사용 가능)
     */
    @GetMapping("/check-nickname/{nickname}")
    public ApiResponse<Boolean> checkNicknameDuplicate(
            @PathVariable String nickname
    ) {
        return ApiResponse.success(characterService.checkNicknameDuplicate(nickname));
    }

    /**
     * 현재 로그인한 회원의 캐릭터 정보를 조회합니다.
     * @param authentication 현재 인증된 사용자 정보
     * @return 캐릭터 정보
     */
    @GetMapping("/me")
    public ApiResponse<CharacterResponse> getMyCharacter(
            Authentication authentication
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String memberId = authentication.getPrincipal().toString();
        log.debug("Fetching character for member: {}", memberId);

        com.ssafy.roCatRun.domain.character.entity.Character character =
                characterService.getCharacterByMemberId(Long.parseLong(memberId));
        return ApiResponse.success(new CharacterResponse(character));
    }

    /**
     * 캐릭터의 닉네임을 수정합니다.
     * @param authentication 현재 인증된 사용자 정보
     * @param newNickname 변경할 새로운 닉네임
     * @return 수정 완료 응답
     */
    @PatchMapping("/nickname")
    public ApiResponse<Void> updateNickname(
            Authentication authentication,
            @RequestParam String newNickname
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String memberId = authentication.getPrincipal().toString();
        log.debug("Updating nickname for member: {}", memberId);

        characterService.updateNickname(Long.parseLong(memberId), newNickname);
        return ApiResponse.success(null);
    }
}