package com.ssafy.roCatRun.domain.character.service;

import com.ssafy.roCatRun.domain.character.dto.request.CharacterCreateRequest;
import com.ssafy.roCatRun.domain.character.entity.Character;
import com.ssafy.roCatRun.domain.character.repository.CharacterRepository;
import com.ssafy.roCatRun.domain.member.entity.Member;
import com.ssafy.roCatRun.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CharacterService {
    private final CharacterRepository characterRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        return characterRepository.existsByNickname(nickname);
    }

    @Transactional
    public Character createCharacter(CharacterCreateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String nickname = request.getNickname();

        // 길이 검증
        if (nickname.length() < 2 || nickname.length() > 10) {
            throw new IllegalArgumentException("닉네임은 2자 이상 10자 이하여야 합니다.");
        }

        // 문자 패턴 검증
        if (!nickname.matches("^[a-zA-Z0-9가-힣]*$")) {
            throw new IllegalArgumentException("닉네임은 한글, 영문, 숫자만 사용 가능합니다.");
        }

        if (checkNicknameDuplicate(nickname)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        return characterRepository.save(Character.createCharacter(nickname, member));
    }

    @Transactional
    public void updateNickname(Long characterId, String newNickname) {
        if (checkNicknameDuplicate(newNickname)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));
        character.setNickname(newNickname);
    }

    /**
     * 회원 ID로 캐릭터 정보를 조회합니다.
     * @param memberId 회원 ID
     * @return 해당 회원의 캐릭터 정보
     * @throws IllegalArgumentException 회원이 존재하지 않거나 캐릭터가 없는 경우
     */
    @Transactional(readOnly = true)
    public Character getCharacterByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (member.getCharacter() == null) {
            throw new IllegalArgumentException("캐릭터가 존재하지 않습니다.");
        }

        return member.getCharacter();
    }
}