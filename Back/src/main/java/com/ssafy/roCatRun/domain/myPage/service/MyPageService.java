package com.ssafy.roCatRun.domain.myPage.service;

import com.ssafy.roCatRun.domain.gameCharacter.service.GameCharacterService;
import com.ssafy.roCatRun.domain.member.repository.MemberRepository;
import com.ssafy.roCatRun.domain.myPage.dto.request.MyPageUpdateRequest;
import com.ssafy.roCatRun.domain.myPage.dto.response.MyPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 마이페이지 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final GameCharacterService gameCharacterService;  // GameCharacterService 주입

    /**
     * 마이페이지 정보 조회
     * @param memberId 회원 ID
     * @return 마이페이지 정보
     * @throws IllegalArgumentException 회원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(Long memberId) {
        var member = memberRepository.findByIdWithCharacter(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        var character = member.getGameCharacter();
        return new MyPageResponse(member, character);
    }

    /**
     * 마이페이지 정보 수정
     * null이 아닌 필드만 선택적으로 수정
     * @param memberId 회원 ID
     * @param request 수정할 정보
     * @throws IllegalArgumentException 회원을 찾을 수 없는 경우
     * @throws IllegalStateException 닉네임이 중복되는 경우
     */
    @Transactional
    public void updateMyPageInfo(Long memberId, MyPageUpdateRequest request) {
        var member = memberRepository.findByIdWithCharacter(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        var character = member.getGameCharacter();

        // 닉네임 변경 요청이 있는 경우
        if (request.getNickname() != null && !request.getNickname().equals(character.getNickname())) {
            // GameCharacterService의 중복 확인 메서드 재사용
            if (gameCharacterService.checkNicknameDuplicate(request.getNickname())) {
                throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
            }
            // 닉네임 변경이 유효한 경우 업데이트
            character.updateNickname(request.getNickname());
        }

        // 회원 신체 정보 업데이트 (null이 아닌 필드만)
        if (request.getHeight() != null) {
            member.setHeight(request.getHeight());
        }
        if (request.getWeight() != null) {
            member.setWeight(request.getWeight());
        }
        if (request.getAge() != null) {
            member.setAge(request.getAge());
        }
        if (request.getGender() != null) {
            member.setGender(request.getGender());
        }
    }
}