package com.ssafy.roCatRun.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ExceptionCode errorCode;

    public CustomException(ExceptionCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ExceptionCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}