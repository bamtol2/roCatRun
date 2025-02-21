package com.ssafy.roCatRun.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponse> handleTokenRefreshException(TokenRefreshException e) {
        ErrorResponse response = ErrorResponse.builder()
                .error("Token Refresh Failed")
                .message(e.getMessage())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        ErrorResponse response = ErrorResponse.builder()
                .error("Token Expired")
                .message("Access token has expired")
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.builder()
                .error("Internal Server Error")
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(InvalidNicknameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNicknameException(InvalidNicknameException e) {
        ErrorResponse response = ErrorResponse.builder()
                .error("Invalid Nickname")
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ExceptionCode errorCode = e.getErrorCode();
        int httpStatus = convertToHttpStatus(errorCode.getStatus());

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getCode(),
                e.getMessage(),
                httpStatus  // 변환된 3자리 상태코드 사용
        );
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private int convertToHttpStatus(int status) {
        // 4자리 상태 코드를 3자리 HTTP 상태 코드로 변환
        return status / 10; // 4000 -> 400, 4040 -> 404
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                ExceptionCode.INVALID_INPUT_VALUE.getCode(),
                e.getMessage(),
                ExceptionCode.INVALID_INPUT_VALUE.getStatus()
        );
        return ResponseEntity.status(400).body(errorResponse);
    }

}