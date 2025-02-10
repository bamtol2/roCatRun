package com.ssafy.roCatRun.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerJoinedResponse {
    private String userId;
    private int currentPlayers;
    private int maxPlayers;
}
