package com.ssafy.roCatRun.domain.stats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatsResponse {
    private String userId;
    private String nickName;
    private List<Game> games;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Game {
        private String roomId;
        private String date;
        private String difficulty;  // EASY, NORMAL, HARD
        private boolean result;     // 클리어 여부
        private List<Player> players;
        private GameDetails details;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Player {
        private int rank;
        private String profileUrl;
        private String nickname;
        private double distance;
        private int attackCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameDetails {
        private String pace;
        private int calories;
        private double cadence;
        private double distance;
        private String runningTime;
    }
}
