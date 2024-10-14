package com.deutschmotors.moduleapi.domain.user.business;

import com.deutschmotors.moduleapi.domain.auth.business.AuthBusiness;
import com.deutschmotors.moduleapi.domain.auth.model.AuthRequest;
import com.deutschmotors.moduleapi.domain.user.controller.model.UserLoginRequest;
import com.deutschmotors.moduleapi.domain.user.controller.model.UserResponse;
import com.deutschmotors.moduleapi.domain.user.converter.UserConverter;
import com.deutschmotors.moduleapi.domain.user.service.UserService;
import com.deutschmotors.modulecommon.error.UserErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
import com.deutschmotors.modulecommon.jwt.TokenResponse;
import com.deutschmotors.moduledata.entity.user.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import static com.deutschmotors.moduledata.entity.user.QRoleEntity.roleEntity;
import static com.deutschmotors.moduledata.entity.user.QUserEntity.userEntity;
import static com.deutschmotors.moduledata.entity.user.QUserRoleEntity.userRoleEntity;

@RequiredArgsConstructor
@Service
public class UserBusiness {
    private final JPAQueryFactory queryFactory;

    private final UserService userService;
    private final UserConverter userConverter;

    private final AuthBusiness authBusiness;


    public UserResponse getRegisterUser(String loginId) {
        UserEntity userEntity = userService.getRegisterUser(loginId);
        return userConverter.toResponse(userEntity);
    }

    public TokenResponse login(UserLoginRequest request) {
        UserEntity userEntity = userService.getRegisterUser(request.getLoginId());
        return authBusiness.issueToken(AuthRequest.from(request), userEntity);

    }

    public UserResponse me() {
        UserEntity userEntity = userService.getRegisterUser("user_normal");
        return userConverter.toResponse(userEntity);
    }
}