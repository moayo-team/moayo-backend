package com.moayo.moayobackend.global.response;

import java.time.OffsetDateTime;


// 프로젝트 공통 응답 포맷
public record ApiResponse<T>(
        boolean isSuccess,
        String code,
        String message,
        OffsetDateTime timestamp,
        T result
) {
    public static <T> ApiResponse<T> ok(String code, String message, T result) {
        return new ApiResponse<>(true, code, message, OffsetDateTime.now(), result);
    }

    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, code, message, OffsetDateTime.now(), null);
    }
}