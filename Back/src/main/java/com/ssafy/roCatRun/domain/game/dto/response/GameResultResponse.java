package com.ssafy.roCatRun.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.util.List;

@Getter
@AllArgsConstructor
public class GameResultResponse {
    private final boolean isCleared;
    private final PlayerDetailResult myResult;      // 현재 유저의 상세 결과
    private final List<PlayerResult> playerResults; // 모든 플레이어의 순위 정보
    private final int myRank;

    @Getter
    @AllArgsConstructor
    public static class PlayerDetailResult { // 유저의 러닝 상세
        private String userId;
        private String nickName;
        private String characterImage;
        private Long runningTime;
        private double totalDistance;
        private double paceAvg;
        private double heartRateAvg;
        private double cadenceAvg;
        private int calories;
        private int itemUseCount;
        private int rewardExp;
        private int rewardCoin;
        private final boolean hasLeveledUp;  // 추가
        private final int oldLevel;          // 추가
        private final int newLevel;          // 추가

    }

    @Getter
    @AllArgsConstructor
    public static class PlayerResult { // 다른 유저들 데이터 포함
        private final String userId;
        private final String nickname;
        private final String characterImage;
        private final double totalDistance;
        private final int itemUseCount;
        private final int rewardExp;
        private final int rewardCoin;
        private final boolean hasLeveledUp;  // 추가
        private final int oldLevel;          // 추가
        private final int newLevel;          // 추가
    }
}
