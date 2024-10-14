package com.deutschmotors.moduleapi.domain.user.business;

import com.deutschmotors.moduleapi.domain.user.controller.model.UserLoginRequest;
import com.deutschmotors.moduleapi.domain.user.controller.model.UserResponse;
import com.deutschmotors.moduleapi.domain.user.converter.UserConverter;
import com.deutschmotors.moduleapi.domain.user.service.UserService;
import com.deutschmotors.moduledata.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserBusiness {

    private final UserService userService;
    private final UserConverter userConverter;


    public UserResponse getRegisterUser(String loginId) {
        UserEntity userEntity = userService.getRegisterUser(loginId);
        return userConverter.toResponse(userEntity);
    }

    public UserResponse login(UserLoginRequest request) {
        UserEntity userEntity = userService.getRegisterUser(request.getLoginId());
        return userConverter.toResponse(userEntity);

    }

    public UserResponse me() {
        UserEntity userEntity = userService.getRegisterUser("user_normal");
        return userConverter.toResponse(userEntity);
    }
}