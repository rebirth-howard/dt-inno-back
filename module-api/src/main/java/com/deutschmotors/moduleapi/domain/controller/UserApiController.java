package com.deutschmotors.moduleapi.domain.controller;

import com.deutschmotors.moduleapi.domain.business.UserBusiness;
import com.deutschmotors.moduleapi.domain.controller.model.UserResponse;
import com.deutschmotors.modulecommon.apispec.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    private final UserBusiness userBusiness;

    @GetMapping("/me")
    public Api<UserResponse> me(
    ){
        var response = userBusiness.me();
        return Api.OK(response);
    }
}