package com.ssafy.roCatRun.domain.character.dto.response;

import com.ssafy.roCatRun.domain.character.entity.Character;  // Character 클래스 import 추가
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponse {
    private Long rank;                // 순위
    private String characterImage;    // 캐릭터 이미지
    private String nickname;          // 닉네임
    private Integer level;            // 레벨
    private boolean isCurrentUser;    // 현재 사용자 여부

    public static RankingResponse from(Character character, Long rank, boolean isCurrentUser) {
        return new RankingResponse(
                rank,
                character.getCharacterImage(),
                character.getNickname(),
                character.getLevel(),
                isCurrentUser
        );
    }
}