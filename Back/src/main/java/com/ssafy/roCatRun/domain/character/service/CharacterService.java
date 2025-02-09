package com.ssafy.roCatRun.domain.character.service;

import com.ssafy.roCatRun.domain.character.dto.request.CharacterCreateRequest;
import com.ssafy.roCatRun.domain.character.entity.Character;
import com.ssafy.roCatRun.domain.character.repository.CharacterRepository;
import com.ssafy.roCatRun.domain.member.entity.Member;
import com.ssafy.roCatRun.domain.member.repository.MemberRepository;
import com.ssafy.roCatRun.global.exception.ErrorCode;
import com.ssafy.roCatRun.global.exception.InvalidNicknameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 캐릭터 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterService {
    private final CharacterRepository characterRepository;
    private final MemberRepository memberRepository;

    /**
     * 닉네임 중복 여부를 확인합니다.
     * @param nickname 검사할 닉네임
     * @return 중복 여부 (true: 중복, false: 사용 가능)
     */
    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        return characterRepository.existsByNickname(nickname);
    }

    /**
     * 새로운 캐릭터를 생성합니다.
     * @param request 캐릭터 생성 요청 정보
     * @param memberId 회원 ID
     * @return 생성된 캐릭터
     * @throws IllegalArgumentException 회원을 찾을 수 없거나, 닉네임이 유효하지 않은 경우
     */
    @Transactional
    public Character createCharacter(CharacterCreateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String nickname = request.getNickname();
        validateNickname(nickname);

        // Member 정보 업데이트
        member.setHeight(request.getHeight());
        member.setWeight(request.getWeight());
        member.setAge(request.getAge());
        member.setGender(request.getGender());

        return characterRepository.save(Character.createCharacter(nickname, member));
    }

    /**
     * 캐릭터의 닉네임을 수정합니다.
     * @param memberId 회원 ID
     * @param newNickname 새로운 닉네임
     * @throws IllegalArgumentException 닉네임이 유효하지 않거나, 캐릭터를 찾을 수 없는 경우
     */
    @Transactional
    public void updateNickname(Long memberId, String newNickname) {
        validateNickname(newNickname);
        Character character = getCharacterByMemberId(memberId);
        log.debug("Found character: {}, updating nickname to: {}", character.getNickname(), newNickname);
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

    /**
     * 닉네임의 유효성을 검사합니다.
     * @param nickname 검사할 닉네임
     * @throws IllegalArgumentException 닉네임이 유효하지 않은 경우
     */
    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_EMPTY);
        }

        if (nickname.length() < 2 || nickname.length() > 10) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_LENGTH_INVALID);
        }

        if (!nickname.matches("^[a-zA-Z0-9가-힣]*$")) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_PATTERN_INVALID);
        }

        if (checkNicknameDuplicate(nickname)) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_DUPLICATE);
        }
    }
}