package com.ssafy.roCatRun.domain.stats.controller;

import com.ssafy.roCatRun.domain.stats.dto.response.DailyStatsResponse;
import com.ssafy.roCatRun.domain.stats.dto.response.MonthlyStatsResponse;
import com.ssafy.roCatRun.domain.stats.dto.response.WeeklyStatsResponse;
import com.ssafy.roCatRun.domain.stats.service.GameStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
     * @param authentication 현재 인증된 사용자 정보
     */
    @GetMapping("/daily")
    public ResponseEntity<DailyStatsResponse> getDailyStats(
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        String userId = authentication.getPrincipal().toString();
        return ResponseEntity.ok(gameStatsService.getDailyStats(userId));
    }

    /**
     * 특정일 통계 조회
     * @param authentication 현재 인증된 사용자 정보
     * @param date 조회할 날짜(YYYY-MM-DD)
     */
    @GetMapping("/day")
    public ResponseEntity<DailyStatsResponse> getDayStats(
            Authentication authentication,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        String userId = authentication.getPrincipal().toString();
        return ResponseEntity.ok(gameStatsService.getDayStats(userId, date));
    }

    /**
     * 주별 통계 조회
     * @param authentication 현재 인증된 사용자 정보
     * @param date 조회할 연월(YYYY-MM)
     * @param week 주차 (1~5)
     */
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyStatsResponse> getWeeklyStats(
            Authentication authentication,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth date,
            @RequestParam int week) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String userId = authentication.getPrincipal().toString();
        return ResponseEntity.ok(gameStatsService.getWeeklyStats(userId, date, week));
    }

    /**
     * 월별 통계 조회
     * @param authentication 현재 인증된 사용자 정보
     * @param date 조회할 연월(YYYY-MM)
     */
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyStatsResponse> getMonthlyStats(
            Authentication authentication,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth date) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        String userId = authentication.getPrincipal().toString();
        return ResponseEntity.ok(gameStatsService.getMonthlyStats(userId, date));
    }
}