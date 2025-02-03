package com.ssafy.roCatRun.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private String error;
    private String message;
    private int status;
}
