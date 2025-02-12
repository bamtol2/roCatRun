package com.ssafy.roCatRun.domain.gameCharacter.service;

import com.ssafy.roCatRun.domain.gameCharacter.dto.request.GameCharacterCreateRequest;
import com.ssafy.roCatRun.domain.gameCharacter.dto.response.RankingListResponse;
import com.ssafy.roCatRun.domain.gameCharacter.dto.response.RankingResponse;
import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import com.ssafy.roCatRun.domain.gameCharacter.repository.GameCharacterRepository;
import com.ssafy.roCatRun.domain.member.entity.Member;
import com.ssafy.roCatRun.domain.member.repository.MemberRepository;
import com.ssafy.roCatRun.global.exception.ErrorCode;
import com.ssafy.roCatRun.global.exception.InvalidNicknameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 캐릭터 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j  // 로깅을 위한 어노테이션
@Service  // 스프링 서비스 계층임을 명시
@RequiredArgsConstructor  // final 필드에 대한 생성자 자동 생성
public class GameCharacterService {
    private final GameCharacterRepository gameCharacterRepository;
    private final MemberRepository memberRepository;

    /**
     * 닉네임 중복 여부를 확인합니다.
     * @param nickname 검사할 닉네임
     * @return 중복 여부 (true: 중복, false: 사용 가능)
     */
    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        return gameCharacterRepository.existsByNickname(nickname);
    }

    /**
     * 새로운 캐릭터를 생성합니다.
     * @param request 캐릭터 생성 요청 정보
     * @param memberId 회원 ID
     * @return 생성된 캐릭터
     * @throws IllegalArgumentException 회원을 찾을 수 없거나, 닉네임이 유효하지 않은 경우
     */
    @Transactional
    public GameCharacter createCharacter(GameCharacterCreateRequest request, Long memberId) {
        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String nickname = request.getNickname();
        validateNickname(nickname);  // 닉네임 유효성 검사

        // Member 정보 업데이트
        member.setHeight(request.getHeight());
        member.setWeight(request.getWeight());
        member.setAge(request.getAge());
        member.setGender(request.getGender());

        // 캐릭터 생성 및 저장
        return gameCharacterRepository.save(GameCharacter.createCharacter(nickname, member));
    }

    /**
     * 캐릭터의 닉네임을 수정합니다.
     * @param memberId 회원 ID
     * @param newNickname 새로운 닉네임
     * @throws IllegalArgumentException 닉네임이 유효하지 않거나, 캐릭터를 찾을 수 없는 경우
     */
    @Transactional
    public void updateNickname(Long memberId, String newNickname) {
        validateNickname(newNickname);  // 새 닉네임 유효성 검사
        GameCharacter gameCharacter = getCharacterByMemberId(memberId);
        log.debug("Found character: {}, updating nickname to: {}", gameCharacter.getNickname(), newNickname);
        gameCharacter.setNickname(newNickname);
    }

    /**
     * 회원 ID로 캐릭터 정보를 조회합니다.
     * @param memberId 회원 ID
     * @return 해당 회원의 캐릭터 정보
     * @throws IllegalArgumentException 회원이 존재하지 않거나 캐릭터가 없는 경우
     */
    @Transactional(readOnly = true)
    public GameCharacter getCharacterByMemberId(Long memberId) {
        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 캐릭터 존재 여부 확인
        if (member.getGameCharacter() == null) {
            throw new IllegalArgumentException("캐릭터가 존재하지 않습니다.");
        }

        return member.getGameCharacter();
    }

    /**
     * 캐릭터 랭킹 정보를 조회합니다.
     * @param memberId 현재 로그인한 회원의 ID
     * @return 랭킹 정보 (현재 사용자 랭킹 및 전체 랭킹 리스트)
     */
    @Transactional(readOnly = true)
    public RankingListResponse getRankings(Long memberId) {  // page, size 파라미터 제거
        // 현재 사용자의 캐릭터 조회
        GameCharacter currentGameCharacter = getCharacterByMemberId(memberId);

        // 현재 사용자의 랭킹 정보 조회
        Long myRank = gameCharacterRepository.findRankByLevelAndExperience(
                currentGameCharacter.getLevel(),
                currentGameCharacter.getExperience()
        );

        // 상위 N개의 랭킹 조회 (내 캐릭터 제외)
        List<GameCharacter> topRankings = gameCharacterRepository.findTopNByIdNotOrderByLevelDescExperienceDesc(
                currentGameCharacter.getId(),
                GameCharacterRepository.MAX_RANKING_SIZE  // 상수 사용
        );

        List<RankingResponse> rankingList = topRankings.stream()
                .map(character -> RankingResponse.from(
                        character,
                        gameCharacterRepository.findRankByLevelAndExperience(
                                character.getLevel(),
                                character.getExperience()
                        )
                ))
                .collect(Collectors.toList());

        // 내 랭킹 정보 생성
        RankingResponse myRankingResponse = RankingResponse.from(currentGameCharacter, myRank);

        return new RankingListResponse(myRankingResponse, rankingList);
    }

    /**
     * 닉네임의 유효성을 검사합니다.
     * @param nickname 검사할 닉네임
     * @throws InvalidNicknameException 닉네임이 유효하지 않은 경우
     */
    private void validateNickname(String nickname) {
        // 닉네임 null 또는 공백 체크
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_EMPTY);
        }

        // 닉네임 길이 체크 (2~10자)
        if (nickname.length() < 2 || nickname.length() > 10) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_LENGTH_INVALID);
        }

        // 닉네임 패턴 체크 (영문, 숫자, 한글만 허용)
        if (!nickname.matches("^[a-zA-Z0-9가-힣]*$")) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_PATTERN_INVALID);
        }

        // 닉네임 중복 체크
        if (checkNicknameDuplicate(nickname)) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_DUPLICATE);
        }
    }
}