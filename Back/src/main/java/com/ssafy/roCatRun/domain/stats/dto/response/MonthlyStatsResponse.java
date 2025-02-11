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
public class MonthlyStatsResponse {
    private String userId;
    private String nickName;
    private StatsData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsData {
        private Summary summary;
        private List<MonthlyDailyStat> dailyStats;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private double totalDistance;
        private int totalRun;
        private String averagePace;
        private String totalTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyDailyStat {
        private String date;
        private double distance;
    }
}
