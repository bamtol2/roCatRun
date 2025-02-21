package com.ssafy.roCatRun.domain.game.dto.request;

import com.ssafy.roCatRun.domain.game.entity.raid.BossLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {
    private BossLevel bossLevel;
    private int maxPlayers;
    private boolean isPrivate; // true면 초대 코드로만 입장 가능
}
