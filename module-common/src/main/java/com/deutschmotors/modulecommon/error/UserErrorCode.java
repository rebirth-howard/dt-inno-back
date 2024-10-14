package com.deutschmotors.modulecommon.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Auth의 경우 9200번대 에러코드 사용
 */
@AllArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCodeIfs {

    USER_NOT_FOUND(400 , 9200 , "사용자를 찾을 수 없음"),
    USER_ALREADY_EXIST(400 , 9201 , "사용자가 이미 존재"),

    ;

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String description;
}