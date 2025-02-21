package com.ssafy.roCatRun.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_SOCIAL_TOKEN("유효하지 않은 소셜 토큰입니다"),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
    INVALID_TOKEN("유효하지 않은 토큰입니다"),

    // 닉네임 관련 에러 코드 추가
    NICKNAME_EMPTY("닉네임은 비어있을 수 없습니다"),
    NICKNAME_LENGTH_INVALID("닉네임은 2자 이상 6자 이하여야 합니다"),
    NICKNAME_PATTERN_INVALID("닉네임은 한글, 영문, 숫자만 사용 가능합니다"),
    NICKNAME_DUPLICATE("이미 존재하는 닉네임입니다");

    private final String message;
}