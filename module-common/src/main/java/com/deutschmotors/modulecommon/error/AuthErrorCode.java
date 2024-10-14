package com.deutschmotors.modulecommon.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Token 의 경우 2000번대 에러코드 사용
 */
@AllArgsConstructor
@Getter
public enum AuthErrorCode implements ErrorCodeIfs{

    ILLEGAL_ARGUMENT(400 , 2000 , "Invalid Argument"),
    BAD_CREDENTIALS(400, 2001, "Invalid username or password"),
    USERNAME_NOT_FOUND(400, 2002, "Username not found"),
    ;

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String description;
}