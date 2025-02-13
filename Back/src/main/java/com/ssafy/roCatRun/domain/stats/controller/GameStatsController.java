package com.ssafy.roCatRun.domain.stats.controller;

import com.ssafy.roCatRun.domain.stats.dto.response.DailyStatsResponse;
import com.ssafy.roCatRun.domain.stats.dto.response.MonthlyStatsResponse;
import com.ssafy.roCatRun.domain.stats.dto.response.WeeklyStatsResponse;
import com.ssafy.roCatRun.domain.stats.service.GameStatsService;
import com.ssafy.roCatRun.global.exception.CustomException;
import com.ssafy.roCatRun.global.exception.ExceptionCode;
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
        validateAuthentication(authentication);
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
        validateAuthentication(authentication);
        validateDate(date);
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
        validateAuthentication(authentication);
        validateWeek(week);
        validateYearMonth(date);

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
        validateAuthentication(authentication);
        validateYearMonth(date);
        String userId = authentication.getPrincipal().toString();
        return ResponseEntity.ok(gameStatsService.getMonthlyStats(userId, date));
    }

    private void validateAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CustomException(ExceptionCode.UNAUTHORIZED);
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT_VALUE, "날짜 정보가 없습니다.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new CustomException(ExceptionCode.FUTURE_DATE_NOT_ALLOWED);
        }
    }

    private void validateWeek(int week) {
        if (week < 1 || week > 5) {
            throw new CustomException(ExceptionCode.INVALID_WEEK_VALUE);
        }
    }

    private void validateYearMonth(YearMonth yearMonth) {
        if (yearMonth == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT_VALUE, "연월 정보가 없습니다.");
        }
        if (yearMonth.isAfter(YearMonth.now())) {
            throw new CustomException(ExceptionCode.FUTURE_DATE_NOT_ALLOWED);
        }
    }
}