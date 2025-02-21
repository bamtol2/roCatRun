package com.ssafy.roCatRun.domain.gameCharacter.dto.response;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import lombok.Getter;

/**
 * 게임 캐릭터 정보를 반환하기 위한 Response DTO
 * 클라이언트에게 캐릭터의 상세 정보를 전달
 */
@Getter
public class GameCharacterResponse {
    private final Long id;                     // 캐릭터 고유 ID
    private final String nickname;             // 캐릭터 닉네임
    private final Integer level;               // 현재 레벨
    private final Integer experience;          // 현재 보유 경험치
    private final String characterImage;       // 캐릭터 이미지 경로
    private final Integer coin;                // 보유 코인
    private final Integer totalGames;          // 총 게임 수
    private final Integer wins;                // 승리 수
    private final Integer losses;              // 패배 수
    private final Integer requiredExpForNextLevel;   // 다음 레벨까지 필요한 총 경험치량

    /**
     * GameCharacter 엔티티와 다음 레벨 경험치 정보로 Response 객체를 생성하는 생성자
     * @param gameCharacter 게임 캐릭터 엔티티
     * @param requiredExpForNextLevel 다음 레벨에 필요한 총 경험치
     */
    public GameCharacterResponse(GameCharacter gameCharacter, Integer requiredExpForNextLevel) {
        this.id = gameCharacter.getId();
        this.nickname = gameCharacter.getNickname();
        this.level = gameCharacter.getLevelInfo().getLevel();
        this.experience = gameCharacter.getExperience();
        this.characterImage = gameCharacter.getCharacterImage();
        this.coin = gameCharacter.getCoin();
        this.totalGames = gameCharacter.getTotalGames();
        this.wins = gameCharacter.getWins();
        this.losses = gameCharacter.getLosses();
        this.requiredExpForNextLevel = requiredExpForNextLevel;
    }
}