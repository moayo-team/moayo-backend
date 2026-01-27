package com.moayo.moayobackend.profile.exception;

import com.moayo.moayobackend.global.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/*
 ProfileErrorCode
 - 프로필 도메인 전용 에러코드
 - 공통 에러는 GeneralErrorCode 사용
*/
@Getter
@AllArgsConstructor
public enum ProfileErrorCode implements BaseErrorCode {

    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE404_1", "프로필이 존재하지 않습니다."),
    INDEX_ITEM_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "PROFILE400_1", "기본정보 추가 항목은 최대 4개까지 가능합니다."),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "TAG404_1", "존재하지 않는 관심 태그가 포함되어 있습니다."),
    DOCUMENT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "PROFILE400_2", "학력 첨부 파일은 최대 20개까지 가능합니다."),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "FILE400_1", "파일 용량 제한을 초과했습니다."),
    FILE_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FILE400_2", "허용되지 않은 파일 형식입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
