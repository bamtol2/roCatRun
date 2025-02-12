package com.ssafy.roCatRun.domain.stats.exception;

public class GameStatsNotFoundException extends RuntimeException {
    public GameStatsNotFoundException(String message) {
        super(message);
    }
}
