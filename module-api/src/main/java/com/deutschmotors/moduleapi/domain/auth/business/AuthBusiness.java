package com.deutschmotors.moduleapi.domain.auth.business;

import com.deutschmotors.moduleapi.domain.auth.model.AuthAuthenticationToken;
import com.deutschmotors.moduleapi.domain.auth.model.AuthRequest;
import com.deutschmotors.moduleapi.domain.auth.model.AuthUserDetails;
import com.deutschmotors.moduleapi.domain.auth.service.AuthService;
import com.deutschmotors.modulecommon.error.AuthErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
import com.deutschmotors.modulecommon.jwt.TokenResponse;
import com.deutschmotors.moduledata.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthBusiness {

    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AuthUserDetails loadUserByUsername(String username) {
        return authService.loadUserByUsername(username);
    }

    public TokenResponse issueToken(AuthRequest request, UserEntity userEntity) {

        if (userEntity == null || !passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new ApiException(AuthErrorCode.USERNAME_NOT_FOUND);
        }

        AuthUserDetails oldInfo = extractedBy(userEntity);

        // AuthenticationManager에 loginId 사용
        Authentication authentication = AuthAuthenticationToken.builder()
                .principal(oldInfo)
                .credentials(null)
                .authorities(oldInfo.getUserRoles())
                .authenticated(true)
                .build();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthUserDetails refreshInfo = (AuthUserDetails) authentication.getPrincipal();

        // TODO: 상태값에 따라 처리

        // 인증성공 시 처리
        if (authentication.isAuthenticated()) {
            TokenResponse response = authService.getToken(refreshInfo.toCommonUserDetails());
            return response;
        }

        throw new ApiException(AuthErrorCode.TOKEN_EXCEPTION);
    }

    private AuthUserDetails extractedBy(UserEntity user) {
        Set<GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().getRoleName()))
                .collect(Collectors.toSet());

        return AuthUserDetails.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .userRoles(authorities)
                .build();
    }
}
