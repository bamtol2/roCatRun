package com.ssafy.roCatRun.domain.gameCharacter.dto.response;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;  // Character 클래스 import 추가
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

    public static RankingResponse from(GameCharacter gameCharacter, Long rank, boolean isCurrentUser) {
        return new RankingResponse(
                rank,
                gameCharacter.getCharacterImage(),
                gameCharacter.getNickname(),
                gameCharacter.getLevel(),
                isCurrentUser
        );
    }
}