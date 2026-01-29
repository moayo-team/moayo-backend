package com.moayo.moayobackend.global.exception;

import lombok.Getter;

/*
 BusinessException
 - 서비스 레이어에서 명시적으로 던지는 비즈니스 예외
 - BaseErrorCode를 포함해서 code/message를 ApiResponse로 통일해서 내려줌
*/
@Getter
public class BusinessException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public BusinessException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(BaseErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }
}
