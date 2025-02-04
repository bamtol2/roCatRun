package com.ssafy.raidtest.raid.service;

import com.ssafy.raidtest.raid.domain.statistics.GameResult;
import com.ssafy.raidtest.raid.domain.statistics.StatisticsPeriod;
import com.ssafy.raidtest.raid.dto.GameStatistics;
import com.ssafy.raidtest.raid.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {
    private final StatisticsRepository statisticsRepository;

    public GameStatistics getStatistics(String userId, StatisticsPeriod period) {
        LocalDateTime startDate = calculateStartDate(period);
        List<GameResult> results = statisticsRepository.findByUserIdAndPeriod(userId, startDate);

        if (results.isEmpty()) {
            return createEmptyStatistics();
        }

        return GameStatistics.builder()
                .totalGames(results.size())
                .victories(countVictories(results))
                .mvpCount(countMvp(results, userId))
                .averageClearTime(calculateAverageClearTime(results))
                .bossStats(calculateBossStats(results))
                .build();
    }

    private GameStatistics createEmptyStatistics() {
        return GameStatistics.builder()
                .totalGames(0)
                .victories(0)
                .mvpCount(0)
                .averageClearTime(0)
                .bossStats(Map.of())
                .build();
    }

    private LocalDateTime calculateStartDate(StatisticsPeriod period) {
        LocalDateTime now = LocalDateTime.now();
        return switch (period) {
            case DAILY -> now.minusDays(1);
            case WEEKLY -> now.minusWeeks(1);
            case MONTHLY -> now.minusMonths(1);
        };
    }

    private int countVictories(List<GameResult> results) {
        return (int) results.stream()
                .filter(GameResult::isVictory)
                .count();
    }

    private int countMvp(List<GameResult> results, String userId) {
        return (int) results.stream()
                .filter(r -> userId.equals(r.getMvpUserId()))
                .count();
    }

    private double calculateAverageClearTime(List<GameResult> results) {
        return results.stream()
                .filter(GameResult::isVictory)
                .mapToInt(GameResult::getClearTime)
                .average()
                .orElse(0);
    }

    private Map<Long, GameStatistics.BossStatistics> calculateBossStats(List<GameResult> results) {
        Map<Long, List<GameResult>> bossResults = results.stream()
                .collect(Collectors.groupingBy(r -> r.getBossId()));

        return bossResults.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> createBossStatistics(entry.getValue())
                ));
    }

    private GameStatistics.BossStatistics createBossStatistics(List<GameResult> bossResults) {
        int victories = countVictories(bossResults);
        return GameStatistics.BossStatistics.builder()
                .bossName(bossResults.get(0).getBossName())
                .totalAttempts(bossResults.size())
                .victories(victories)
                .winRate((double) victories / bossResults.size() * 100)
                .fastestClear(calculateFastestClear(bossResults))
                .build();
    }

    private double calculateFastestClear(List<GameResult> results) {
        return results.stream()
                .filter(GameResult::isVictory)
                .mapToInt(GameResult::getClearTime)
                .min()
                .orElse(0);
    }
}
