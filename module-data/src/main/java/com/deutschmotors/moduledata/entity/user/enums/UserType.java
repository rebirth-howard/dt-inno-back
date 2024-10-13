package com.deutschmotors.moduledata.entity.user.enums;

import com.deutschmotors.modulecommon.exception.ApiException;
import com.deutschmotors.modulecommon.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum UserType {
    USER_TYPE_NORMAL("일반회원"),
    USER_TYPE_INSURER("보험사회원"),
    ;

    private final String codeNm;

    private static final Map<String, UserType> map = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(UserType::getCodeNm, Function.identity()))
    );

    public static UserType of(String codeNm) {
        return map.get(codeNm);
    }

    public static UserType fromString(String key) {
        return Arrays.stream(values())
                .filter(type -> type.name().equals(key.toUpperCase()))
                .findAny()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "회원 타입 종류는 " + valueAllPrint() + "입니다."));
    }

    private static String valueAllPrint() {
        return Arrays.stream(UserType.values())
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }
}
