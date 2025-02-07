package com.ssafy.roCatRun.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_SOCIAL_TOKEN("유효하지 않은 소셜 토큰입니다"),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
    INVALID_TOKEN("유효하지 않은 토큰입니다");

    private final String message;
}