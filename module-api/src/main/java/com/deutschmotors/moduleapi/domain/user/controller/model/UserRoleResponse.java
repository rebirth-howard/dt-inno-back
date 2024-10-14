package com.deutschmotors.moduleapi.domain.user.controller.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRoleResponse {

    private Long id;
    private String roleName;

}
