package com.ssafy.roCatRun.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameOverResponse {
    private boolean isGameOver;
    private String message = "게임이 종료되었습니다.";
}
