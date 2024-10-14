package com.deutschmotors.moduleapi.domain.user.controller;

import com.deutschmotors.moduleapi.domain.user.business.UserBusiness;
import com.deutschmotors.moduleapi.domain.user.controller.model.UserLoginRequest;
import com.deutschmotors.moduleapi.domain.user.controller.model.UserResponse;
import com.deutschmotors.modulecommon.apispec.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/open-api/user")
public class UserOpenApiController {

    private final UserBusiness userBusiness;

    @PostMapping("/login")
    public Api<UserResponse> login(
            @Valid
            @RequestBody Api<UserLoginRequest> request
    ) {
        UserResponse response = userBusiness.login(request.getBody());
        return Api.OK(response);
    }

}
