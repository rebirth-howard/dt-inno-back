package com.deutschmotors.moduleapi.domain.user.controller.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginRequest {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;

    public static UserLoginRequest of(String loginId, String password) {
        return new UserLoginRequest(loginId, password);
    }

}