package com.ssafy.roCatRun.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    // Common
    INVALID_INPUT_VALUE(4000, "INVALID_INPUT_VALUE", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(5000, "SERVER_ERROR", "서버 내부 오류가 발생했습니다."),

    // Auth
    UNAUTHORIZED(4001, "UNAUTHORIZED", "인증 정보가 없습니다."),
    INVALID_TOKEN(4002, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),

    // Game Stats
    STATS_NOT_FOUND(4040, "STATS_NOT_FOUND", "게임 기록을 찾을 수 없습니다."),
    INVALID_DATE_RANGE(4003, "INVALID_DATE_RANGE", "유효하지 않은 날짜 범위입니다."),
    INVALID_WEEK_VALUE(4004, "INVALID_WEEK_VALUE", "유효하지 않은 주차 값입니다."),
    FUTURE_DATE_NOT_ALLOWED(4005, "FUTURE_DATE_NOT_ALLOWED", "미래 날짜는 조회할 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}