package com.ssafy.roCatRun.domain.gameCharacter.dto.response;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import lombok.Getter;

@Getter
public class GameCharacterResponse {
    private final Long id;
    private final String nickname;
    private final Integer level;
    private final Integer experience;
    private final String characterImage;
    private final Integer coin;
    private final Integer totalGames;
    private final Integer wins;
    private final Integer losses;

    public GameCharacterResponse(GameCharacter gameCharacter) {
        this.id = gameCharacter.getId();
        this.nickname = gameCharacter.getNickname();
        this.level = gameCharacter.getLevel();
        this.experience = gameCharacter.getExperience();
        this.characterImage = gameCharacter.getCharacterImage();
        this.coin = gameCharacter.getCoin();
        this.totalGames = gameCharacter.getTotalGames();
        this.wins = gameCharacter.getWins();
        this.losses = gameCharacter.getLosses();
    }
}