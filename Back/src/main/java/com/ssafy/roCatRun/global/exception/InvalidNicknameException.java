package com.ssafy.roCatRun.global.exception;

import lombok.Getter;

@Getter
public class InvalidNicknameException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidNicknameException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}