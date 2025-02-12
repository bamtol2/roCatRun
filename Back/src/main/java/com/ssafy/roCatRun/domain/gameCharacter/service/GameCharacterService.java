package com.ssafy.roCatRun.domain.gameCharacter.service;

import com.ssafy.roCatRun.domain.gameCharacter.dto.request.GameCharacterCreateRequest;
import com.ssafy.roCatRun.domain.gameCharacter.dto.response.GameCharacterResponse;
import com.ssafy.roCatRun.domain.gameCharacter.dto.response.RankingListResponse;
import com.ssafy.roCatRun.domain.gameCharacter.dto.response.RankingResponse;
import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import com.ssafy.roCatRun.domain.gameCharacter.entity.Level;
import com.ssafy.roCatRun.domain.gameCharacter.repository.GameCharacterRepository;
import com.ssafy.roCatRun.domain.gameCharacter.repository.LevelRepository;
import com.ssafy.roCatRun.domain.member.entity.Member;
import com.ssafy.roCatRun.domain.member.repository.MemberRepository;
import com.ssafy.roCatRun.global.exception.ErrorCode;
import com.ssafy.roCatRun.global.exception.InvalidNicknameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 캐릭터 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameCharacterService {
    private final GameCharacterRepository gameCharacterRepository;
    private final MemberRepository memberRepository;
    private final LevelRepository levelRepository;

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
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String nickname = request.getNickname();
        validateNickname(nickname);

        member.setHeight(request.getHeight());
        member.setWeight(request.getWeight());
        member.setAge(request.getAge());
        member.setGender(request.getGender());

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
        validateNickname(newNickname);
        GameCharacter gameCharacter = getCharacterByMemberId(memberId);
        log.debug("Found character: {}, updating nickname to: {}", gameCharacter.getNickname(), newNickname);
        gameCharacter.setNickname(newNickname);
    }

    /**
     * 회원 ID로 캐릭터 정보를 조회합니다.
     * @param memberId 회원 ID
     * @return 해당 회원의 캐릭터
     * @throws IllegalArgumentException 회원이 존재하지 않거나 캐릭터가 없는 경우
     */
    @Transactional(readOnly = true)
    public GameCharacter getCharacterByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

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
    public RankingListResponse getRankings(Long memberId) {
        GameCharacter currentGameCharacter = getCharacterByMemberId(memberId);

        Long myRank = gameCharacterRepository.findRankByLevelAndExperience(
                currentGameCharacter.getLevel(),
                currentGameCharacter.getExperience()
        );

        List<GameCharacter> topRankings = gameCharacterRepository.findTopNByIdNotOrderByLevelDescExperienceDesc(
                currentGameCharacter.getId(),
                GameCharacterRepository.MAX_RANKING_SIZE
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

        RankingResponse myRankingResponse = RankingResponse.from(currentGameCharacter, myRank);

        return new RankingListResponse(myRankingResponse, rankingList);
    }

    /**
     * 닉네임의 유효성을 검사합니다.
     * @param nickname 검사할 닉네임
     * @throws InvalidNicknameException 닉네임이 유효하지 않은 경우
     */
    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_EMPTY);
        }

        if (nickname.length() < 2 || nickname.length() > 6) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_LENGTH_INVALID);
        }

        if (!nickname.matches("^[a-zA-Z0-9가-힣]*$")) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_PATTERN_INVALID);
        }

        if (checkNicknameDuplicate(nickname)) {
            throw new InvalidNicknameException(ErrorCode.NICKNAME_DUPLICATE);
        }
    }

    /**
     * 캐릭터 정보와 다음 레벨 경험치 정보를 포함한 응답을 생성합니다.
     * @param gameCharacter 게임 캐릭터 엔티티
     * @return 캐릭터 정보 응답 객체
     */
    public GameCharacterResponse createGameCharacterResponse(GameCharacter gameCharacter) {
        Level nextLevel = levelRepository.findByLevel(gameCharacter.getLevel() + 1);
        Integer requiredExp = nextLevel != null ? nextLevel.getRequiredExp() : null;

        return new GameCharacterResponse(gameCharacter, requiredExp);
    }

    /**
     * 회원 ID로 캐릭터 정보를 조회하고 다음 레벨 경험치 정보를 포함한 응답을 생성합니다.
     * @param memberId 회원 ID
     * @return 캐릭터 정보 응답 객체
     */
    @Transactional(readOnly = true)
    public GameCharacterResponse getCharacterResponseByMemberId(Long memberId) {
        GameCharacter character = getCharacterByMemberId(memberId);
        return createGameCharacterResponse(character);
    }
}