package com.deutschmotors.moduleapi.domain.business;

import com.deutschmotors.moduleapi.domain.controller.model.UserLoginRequest;
import com.deutschmotors.moduleapi.domain.controller.model.UserResponse;
import com.deutschmotors.moduleapi.domain.converter.UserConverter;
import com.deutschmotors.moduleapi.domain.service.UserService;
import com.deutschmotors.moduledata.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserBusiness {

    private final UserService userService;
    private final UserConverter userConverter;

    public UserResponse login(UserLoginRequest request) {
        UserEntity userEntity = userService.getRegisterUser(request.getLoginId());
        return userConverter.toResponse(userEntity);

    }

    public UserResponse me() {
        UserEntity userEntity = userService.getRegisterUser("user_normal");
        return userConverter.toResponse(userEntity);
    }
}