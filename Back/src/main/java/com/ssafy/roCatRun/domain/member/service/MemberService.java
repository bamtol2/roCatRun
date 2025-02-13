package com.ssafy.roCatRun.domain.member.service;

import com.ssafy.roCatRun.domain.gameCharacter.repository.GameCharacterRepository;
import com.ssafy.roCatRun.domain.member.dto.request.MemberProfileUpdateRequest;
import com.ssafy.roCatRun.domain.member.entity.Member;
import com.ssafy.roCatRun.domain.member.repository.MemberRepository;
import com.ssafy.roCatRun.domain.member.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GameCharacterRepository gameCharacterRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    /**
     * 회원 탈퇴 처리
     * - Member 엔티티 삭제 시 CascadeType.ALL 설정으로 인해 연관된 엔티티들이 자동 삭제됨:
     *   - GameCharacter (orphanRemoval = true)
     *   - GameResult (CascadeType.ALL)
     *   - Inventory (CascadeType.ALL)
     * - Redis에 저장된 토큰 정보 삭제
     */
    @Transactional
    public void deleteMember(Long memberId) {
        // 리프레시 토큰 삭제
        refreshTokenRedisRepository.deleteByKey(memberId.toString());

        // 회원 삭제 (연관된 엔티티들은 cascade로 자동 삭제)
        memberRepository.deleteById(memberId);
    }

    @Transactional
    public void logout(Long memberId){
        // redis에서 refreshToken 삭제
        refreshTokenRedisRepository.deleteByKey(memberId.toString());
    }


    @Transactional
    public void updateMemberProfile(Long memberId, MemberProfileUpdateRequest request) {
        Member member = memberRepository.findByIdWithCharacter(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // null이 아닌 필드만 업데이트
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
