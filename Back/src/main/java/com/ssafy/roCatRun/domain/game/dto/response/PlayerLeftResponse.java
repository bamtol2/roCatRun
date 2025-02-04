package com.ssafy.roCatRun.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerLeftResponse {
    private String userId;
    private int remainingPlayers;
    private int maxPlayers;
}