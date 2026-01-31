package com.moayo.moayobackend.global.exception;

import com.moayo.moayobackend.global.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/*
 GlobalExceptionHandler
 - 모든 예외를 프로젝트 공통 응답(ApiResponse)으로 변환
 - BusinessException(BaseErrorCode)을 받아서 code/message를 일관되게 내려줌
*/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException e) {
        BaseErrorCode ec = e.getErrorCode();
        return ApiResponse.fail(ec.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(this::fieldErrorToMessage)
                .collect(Collectors.joining(", "));
        return ApiResponse.fail(GeneralErrorCode.BAD_REQUEST.getCode(), msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Void> handleConstraint(ConstraintViolationException e) {
        return ApiResponse.fail(GeneralErrorCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<Void> handleAuth(AuthenticationException e) {
        return ApiResponse.fail(
                GeneralErrorCode.UNAUTHORIZED.getCode(),
                GeneralErrorCode.UNAUTHORIZED.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleDenied(AccessDeniedException e) {
        return ApiResponse.fail(
                GeneralErrorCode.FORBIDDEN.getCode(),
                GeneralErrorCode.FORBIDDEN.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException e) {
        return ApiResponse.fail(GeneralErrorCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ApiResponse<Void> handleIllegalState(IllegalStateException e) {
        return ApiResponse.fail(GeneralErrorCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleAny(Exception e) {
        return ApiResponse.fail("SERVER500_1", "서버 오류가 발생했습니다.");
    }

    private String fieldErrorToMessage(FieldError fe) {
        String field = fe.getField();
        String msg = fe.getDefaultMessage();
        if (msg == null || msg.isBlank()) {
            return field + " 값이 올바르지 않습니다.";
        }
        return field + ": " + msg;
    }
}
