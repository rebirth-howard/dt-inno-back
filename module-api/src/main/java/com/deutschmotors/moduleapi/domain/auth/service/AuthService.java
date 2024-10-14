package com.deutschmotors.moduleapi.domain.auth.service;

import com.deutschmotors.moduleapi.domain.auth.model.AuthUserDetails;
import com.deutschmotors.moduleapi.domain.user.converter.UserConverter;
import com.deutschmotors.modulecommon.error.AuthErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
import com.deutschmotors.modulecommon.jwt.CommonUserDetails;
import com.deutschmotors.modulecommon.jwt.TokenHelper;
import com.deutschmotors.modulecommon.jwt.TokenResponse;
import com.deutschmotors.moduledata.entity.user.UserEntity;
import com.deutschmotors.moduledata.repository.user.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.deutschmotors.moduledata.entity.user.QRoleEntity.roleEntity;
import static com.deutschmotors.moduledata.entity.user.QUserEntity.userEntity;
import static com.deutschmotors.moduledata.entity.user.QUserRoleEntity.userRoleEntity;

@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {

    private final TokenHelper tokenHelper;
    private final JPAQueryFactory queryFactory;

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Transactional
    @Override
    public AuthUserDetails loadUserByUsername(String username) {

        UserEntity user = queryFactory.selectFrom(userEntity)
                .leftJoin(userEntity.userRoles, userRoleEntity).fetchJoin()
                .leftJoin(userRoleEntity.role, roleEntity).fetchJoin()
                .where(userEntity.loginId.eq(username))
                .fetchOne();

        if (user == null) {
            throw new ApiException(AuthErrorCode.USERNAME_NOT_FOUND);
        }

        return AuthUserDetails.from(user);

    }

    public TokenResponse getToken(CommonUserDetails userDetails) {
        return tokenHelper.generateToken(userDetails);
    }

}
