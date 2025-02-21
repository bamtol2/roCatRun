package com.ssafy.roCatRun.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameStatusResponse {
    private int bossHealth;
    private boolean isFeverTimeActive;
    private String userId;
    private String nickName;
    private int itemUseCount;
}
