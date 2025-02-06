package com.ssafy.roCatRun.domain.member.service;

import com.ssafy.roCatRun.domain.character.repository.CharacterRepository;
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
    private final CharacterRepository characterRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    /**
     * 회원 탈퇴 처리
     * - 캐릭터 정보 삭제
     * - Redis에 저장된 토큰 정보 삭제
     * - 회원 정보 삭제
     */
    @Transactional
    public void deleteMember(Long memberId) {
        // 캐릭터 삭제
        characterRepository.deleteByMember_Id(memberId);

        // 리프레시 토큰 삭제
        refreshTokenRedisRepository.deleteByKey(memberId.toString());

        // 회원 삭제
        memberRepository.deleteById(memberId);
    }
}