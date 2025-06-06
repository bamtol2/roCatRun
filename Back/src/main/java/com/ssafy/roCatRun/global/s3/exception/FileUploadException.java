package com.ssafy.roCatRun.global.s3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
}
