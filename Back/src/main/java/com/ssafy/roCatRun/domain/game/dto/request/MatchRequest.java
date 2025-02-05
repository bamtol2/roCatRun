package com.ssafy.roCatRun.domain.game.dto.request;

import com.ssafy.roCatRun.domain.game.entity.raid.BossLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchRequest {
    private BossLevel bossLevel;
    private int maxPlayers;
}
