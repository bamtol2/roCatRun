package com.ssafy.roCatRun.global.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
//API 응답의 일관성을 유지하기 위한 wrapper 클래스
@Getter
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success; // 성공, 실패 나타내는
        this.message = message;
        this.data = data;
    }

    // 성공 응답 생성
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    // 성공 응답 생성 (메시지 포함)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // 실패 응답 생성
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}