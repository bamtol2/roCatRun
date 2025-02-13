package com.ssafy.roCatRun.domain.stats.exception;

import com.ssafy.roCatRun.global.exception.CustomException;
import com.ssafy.roCatRun.global.exception.ErrorCode;
import com.ssafy.roCatRun.global.exception.ExceptionCode;

public class GameStatsNotFoundException extends CustomException {
    public GameStatsNotFoundException(String message) {
        super(ExceptionCode.STATS_NOT_FOUND, message);
    }
}