package com.ssafy.roCatRun.domain.stats.controller;

import com.ssafy.roCatRun.domain.stats.dto.response.DailyStatsResponse;
import com.ssafy.roCatRun.domain.stats.dto.response.MonthlyStatsResponse;
import com.ssafy.roCatRun.domain.stats.dto.response.WeeklyStatsResponse;
import com.ssafy.roCatRun.domain.stats.service.GameStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/statistics/running-stats")
@RequiredArgsConstructor
public class GameStatsController {
    private final GameStatsService gameStatsService;

    /**
     * 일별 통계 조회
     * @param userId 유저 ID
     * @param date 조회할 날짜(YYYY-MM-DD)
     */
    @GetMapping("/daily/{userId}")
    public ResponseEntity<DailyStatsResponse> getDailyStats(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ResponseEntity.ok(gameStatsService.getDailyStats(userId, date));
    }

    /**
     * 주별 통계 조회
     * @param userId 유저 ID
     * @param date 해당 주가 포함하는 날짜(YYYY-MM-DD)
     */
    @GetMapping("/weekly/{userId}")
    public ResponseEntity<WeeklyStatsResponse> getWeeklyStats(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ResponseEntity.ok(gameStatsService.getWeeklyStats(userId, date));
    }

    /**
     * 월별 통계 조회
     * @param userId 유저 ID
     * @param yearMonth 조회할 연월(YYYY-MM)
     */
    @GetMapping("/monthly/{userId}")
    public ResponseEntity<MonthlyStatsResponse> getMonthlyStats(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        return ResponseEntity.ok(gameStatsService.getMonthlyStats(userId, yearMonth));
    }
}