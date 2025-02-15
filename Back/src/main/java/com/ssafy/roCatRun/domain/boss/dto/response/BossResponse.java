package com.ssafy.roCatRun.domain.boss.dto.response;

import com.ssafy.roCatRun.domain.boss.entity.Boss;
import com.ssafy.roCatRun.domain.boss.entity.BossDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BossResponse {
    private List<Boss> bosses;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Boss {
        private BossDifficulty difficulty;
        private int timeLimit;
        private int hpPerKm;
        private String distance;
        private String bossImage;
        private String bossName;
        private int expRewardMin;
        private int expRewardMax;
        private String feverCondition;
        private int coinRewardMin;
        private int coinRewardMax;
    }
}
