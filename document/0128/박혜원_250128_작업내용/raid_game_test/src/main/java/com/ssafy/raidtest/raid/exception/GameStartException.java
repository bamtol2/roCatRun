package com.ssafy.raidtest.raid.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 400
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GameStartException extends RuntimeException {
    public GameStartException(String message) {
        super(message);
    }
}
