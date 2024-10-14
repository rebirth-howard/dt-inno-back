package com.deutschmotors.moduleapi.domain.auth.service;

import com.deutschmotors.moduleapi.domain.auth.model.AuthUserDetails;
import com.deutschmotors.moduleapi.domain.user.converter.UserConverter;
import com.deutschmotors.modulecommon.error.AuthErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
import com.deutschmotors.moduledata.entity.user.UserEntity;
import com.deutschmotors.moduledata.repository.user.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static com.deutschmotors.moduledata.entity.user.QRoleEntity.roleEntity;
import static com.deutschmotors.moduledata.entity.user.QUserEntity.userEntity;
import static com.deutschmotors.moduledata.entity.user.QUserRoleEntity.userRoleEntity;

@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {

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

        return createUserDetails(user);

    }

    private AuthUserDetails createUserDetails(UserEntity user) {
        Set<GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleName()))
                .collect(Collectors.toSet());

        AuthUserDetails authUserDetails = AuthUserDetails.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .userRoles(authorities)
                .build();

        return authUserDetails;
    }

}
