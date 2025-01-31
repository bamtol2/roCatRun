package com.ssafy.raidtest.raid.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


// 404
@ResponseStatus(HttpStatus.NOT_FOUND)
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String gameId) {
        super("게임을 찾을 수 없습니다: " + gameId);
    }
}