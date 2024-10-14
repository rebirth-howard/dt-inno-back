package com.deutschmotors.moduleapi.domain.user.converter;

import com.deutschmotors.moduleapi.domain.user.controller.model.UserResponse;
import com.deutschmotors.moduleapi.domain.user.controller.model.UserRoleResponse;
import com.deutschmotors.modulecommon.error.ErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
import com.deutschmotors.moduledata.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserConverter {

    public UserResponse toResponse(UserEntity userEntity) {
        return Optional.ofNullable(userEntity)
                .map(entity -> UserResponse.of(
                        entity.getId(),
                        entity.getLoginId(),
                        entity.getUsername(),
                        entity.getPassword(),
                        entity.getEmail(),
                        entity.getStatus(),
                        entity.getCreatedBy(),
                        entity.getCreatedAt(),
                        entity.getUpdatedBy(),
                        entity.getUpdatedAt(),
                        entity.getUserRoles().stream()
                                .map(userRoleEntity -> UserRoleResponse.builder()
                                        .id(userRoleEntity.getRole().getId())
                                        .roleName(userRoleEntity.getRole().getRoleName())
                                        .build())
                                .collect(Collectors.toSet())
                ))
                .orElseThrow(() -> new ApiException(ErrorCode.NULL_POINT, "UserEntity Null"));
    }

}
