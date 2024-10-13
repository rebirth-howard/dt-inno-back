package com.deutschmotors.moduledata.entity.user.enums;

import com.deutschmotors.modulecommon.error.ErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
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
public enum UserStatus {
    UNAPPROVED("미승인"),
    UNDER_REVIEW("심사중"),
    REVIEW_REJECTED("심사반려"),
    APPROVED("승인"),
    SUSPENDED("활동정지"),
    PENDING_WITHDRAWAL("탈퇴예정"),
    WITHDRAWN("탈퇴확정")
    ;

    private final String codeNm;

    private static final Map<String, UserStatus> map = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(UserStatus::getCodeNm, Function.identity()))
    );

    public static UserStatus of(String codeNm) {
        return map.get(codeNm);
    }

    public static UserStatus fromString(String key) {
        return Arrays.stream(values())
                .filter(type -> type.name().equals(key.toUpperCase()))
                .findAny()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "회원상태 종류는 " + valueAllPrint() + "입니다."));
    }

    private static String valueAllPrint() {
        return Arrays.stream(UserType.values())
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

}
