package com.deutschmotors.moduleapi.domain.user.controller.model;


import com.deutschmotors.moduledata.entity.user.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    private UUID id;
    private String loginId;
    private String username;
    private String password;
    private String email;
    private UserStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private Set<UserRoleResponse> userRoles;

    public static UserResponse of(UUID id,
                        String loginId,
                        String username,
                        String password,
                        String email,
                        UserStatus status,
                        String createdBy,
                        LocalDateTime createdAt,
                        String updatedBy,
                        LocalDateTime updatedAt,
                        Set<UserRoleResponse> userRoles) {

        return UserResponse.builder()
                .id(id)
                .loginId(loginId)
                .username(username)
                .password(password)
                .email(email)
                .status(status)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .updatedBy(updatedBy)
                .updatedAt(updatedAt)
                .userRoles(userRoles)
                .build();
    }

}