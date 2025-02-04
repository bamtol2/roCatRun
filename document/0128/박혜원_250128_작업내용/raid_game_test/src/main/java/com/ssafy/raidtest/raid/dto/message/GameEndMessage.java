package com.ssafy.raidtest.raid.dto.message;

import com.ssafy.raidtest.raid.dto.PlayerStats;
import com.ssafy.raidtest.raid.dto.Reward;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class GameEndMessage {
    private String gameId;
    private boolean victory;
    private String mvpUserId;
    private Map<String, PlayerStats> playerStats;
    private Map<String, List<Reward>> rewards;
}
