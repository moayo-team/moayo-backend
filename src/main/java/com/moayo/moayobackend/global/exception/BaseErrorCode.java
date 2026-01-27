package com.moayo.moayobackend.global.exception;

import org.springframework.http.HttpStatus;

/*
 BaseErrorCode
 - 프로젝트 전역에서 사용하는 에러코드 인터페이스
 - 모든 도메인 에러코드는 이 인터페이스를 구현하도록 통일
*/
public interface BaseErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
