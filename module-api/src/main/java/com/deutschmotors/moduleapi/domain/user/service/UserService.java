package com.deutschmotors.moduleapi.domain.user.service;

import com.deutschmotors.modulecommon.error.UserErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
import com.deutschmotors.moduledata.entity.user.UserEntity;
import com.deutschmotors.moduledata.repository.user.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import static com.deutschmotors.moduledata.entity.user.QRoleEntity.roleEntity;
import static com.deutschmotors.moduledata.entity.user.QUserEntity.userEntity;
import static com.deutschmotors.moduledata.entity.user.QUserRoleEntity.userRoleEntity;

@RequiredArgsConstructor
@Service
public class UserService {

    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;

    public UserEntity getRegisterUser(String loginId) {

        UserEntity user = queryFactory.selectFrom(userEntity)
                .leftJoin(userEntity.userRoles, userRoleEntity).fetchJoin()
                .leftJoin(userRoleEntity.role, roleEntity).fetchJoin()
                .where(userEntity.loginId.eq(loginId))
                .fetchOne();

        if (ObjectUtils.isEmpty(user)) {
            throw new ApiException(UserErrorCode.USER_NOT_FOUND);
        }

        return user;
    }

}