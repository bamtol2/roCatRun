package com.ssafy.raidtest.raid.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
public class GameStatistics {
    private int totalGames;         // 전체 게임 수
    private int victories;          // 승리 횟수
    private int mvpCount;           // MVP 횟수
    private double averageClearTime; // 평균 클리어 시간
    private Map<Long, BossStatistics> bossStats; // 보스별 통계

    @Data
    @Builder
    public static class BossStatistics {
        private String bossName;
        private int totalAttempts;
        private int victories;
        private double winRate;
        private double fastestClear;
    }
}
