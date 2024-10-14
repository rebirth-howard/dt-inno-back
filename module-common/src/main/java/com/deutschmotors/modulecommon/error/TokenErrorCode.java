package com.deutschmotors.modulecommon.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Token 의 경우 9000번대 에러코드 사용
 */
@AllArgsConstructor
@Getter
public enum TokenErrorCode implements ErrorCodeIfs{

    INVALID_TOKEN(400 , 9000 , "유효하지 않은 토큰"),
    EXPIRED_TOKEN(400, 9001, "만료된 토큰"),
    TOKEN_EXCEPTION(400, 9002, "토큰 알수없는 에러"),
    AUTHORIZATION_TOKEN_NOT_FOUND(400, 9003, "인증 헤더 토큰 없음"),

    ;

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String description;
}