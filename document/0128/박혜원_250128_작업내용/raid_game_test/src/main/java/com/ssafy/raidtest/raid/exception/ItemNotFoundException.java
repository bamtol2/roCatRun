package com.ssafy.raidtest.raid.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String itemId) {
        super("아이템을 찾을 수 없습니다: " + itemId);
    }
}
