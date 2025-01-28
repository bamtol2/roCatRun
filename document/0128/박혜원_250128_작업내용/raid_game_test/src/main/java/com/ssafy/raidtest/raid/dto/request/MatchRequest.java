package com.ssafy.raidtest.raid.dto.request;

import com.ssafy.raidtest.raid.domain.boss.Boss;
import lombok.Data;

@Data
public class MatchRequest {
    private String userId;
    private Boss boss;
    private int maxPlayers;
}