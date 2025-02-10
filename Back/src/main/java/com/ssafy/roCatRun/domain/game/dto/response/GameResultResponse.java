package com.ssafy.roCatRun.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.util.List;

@Getter
@AllArgsConstructor
public class GameResultResponse {
    private boolean isCleared;
    private List<PlayerResult> playerResults;

    @Getter
    @AllArgsConstructor
    public static class PlayerResult {
        private String userId;
        private Duration runningTime;
        private double totalDistance;
        private double paceAvg;
        private double heartRateAvg;
        private double cadenceAvg;
        private int calories;
        private int itemUseCount;
    }
}
