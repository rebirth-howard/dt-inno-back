package com.deutschmotors.moduleapi.domain.auth.model;

import com.deutschmotors.moduleapi.domain.user.controller.model.UserLoginRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthRequest {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;

    public static AuthRequest of(String loginId, String password){
        return new AuthRequest(loginId, password);
    }

    public static AuthRequest from(UserLoginRequest dto) {
        return of(dto.getLoginId(), dto.getPassword());
    }
}
