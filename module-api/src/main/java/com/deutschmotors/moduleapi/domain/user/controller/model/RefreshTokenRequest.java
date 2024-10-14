package com.deutschmotors.moduleapi.domain.user.controller.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;

    public static RefreshTokenRequest of(String refreshToken) {
        return new RefreshTokenRequest(refreshToken);
    }

}