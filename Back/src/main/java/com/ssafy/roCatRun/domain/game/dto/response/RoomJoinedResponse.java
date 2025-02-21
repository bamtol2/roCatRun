package com.ssafy.roCatRun.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomJoinedResponse {
    private String roomId;
    private String inviteCode;
    private int currentPlayers;
    private int maxPlayers;
}
