package com.ssafy.roCatRun.domain.character.dto.response;

import com.ssafy.roCatRun.domain.character.entity.Character;  // 명시적으로 Character 엔티티 import
import lombok.Getter;

@Getter
public class CharacterResponse {
    private final Long id;
    private final String nickname;
    private final Integer level;
    private final Integer experience;
    private final String characterImage;
    private final Integer coin;

    public CharacterResponse(com.ssafy.roCatRun.domain.character.entity.Character character) {  // 패키지 경로 명시
        this.id = character.getId();
        this.nickname = character.getNickname();
        this.level = character.getLevel();
        this.experience = character.getExperience();
        this.characterImage = character.getCharacterImage();
        this.coin = character.getCoin();
    }
}