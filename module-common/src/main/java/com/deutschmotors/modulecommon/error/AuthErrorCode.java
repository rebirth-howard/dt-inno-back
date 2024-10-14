package com.deutschmotors.modulecommon.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 인증의 경우 9000번대 에러코드 사용
 * 일부 에러코드는 JwtAuthenticationFilter와 싱크를 맞춰야 함.
 */
@AllArgsConstructor
@Getter
public enum AuthErrorCode implements ErrorCodeIfs{
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value() , 9000 , "유효하지 않은 토큰"),
    WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED.value(), 9001, "잘못된 유형의 토큰"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), 9002, "만료된 토큰"),
    AUTHORIZATION_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), 9003, "인증 헤더 토큰 없음"),
    ILLEGAL_ARGUMENT(HttpStatus.UNAUTHORIZED.value(), 9004 , "Invalid Argument"),
    USERNAME_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), 9006, "Invalid username or password"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED.value(), 9005, "Invalid username or password"),

    USER_ACCESS_FORBIDDEN(400 , 9097 , "유효한 회원이 아닙니다."),
    TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), 9098, "토큰 알수없는 에러"),
    AUTH_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), 9099, "알수없는 인증 에러"),
    ;

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String description;
}