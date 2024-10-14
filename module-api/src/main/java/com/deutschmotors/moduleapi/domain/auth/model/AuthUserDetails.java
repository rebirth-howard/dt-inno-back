package com.deutschmotors.moduleapi.domain.auth.model;

import com.deutschmotors.moduleapi.domain.user.controller.model.UserResponse;
import com.deutschmotors.moduledata.entity.user.enums.UserStatus;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthUserDetails implements UserDetails {

    private UUID id;
    private String loginId;
    private String username;
    private String password;
    private String email;
    private UserStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private Set<GrantedAuthority> userRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != UserStatus.WITHDRAWN; // 탈퇴 상태가 아닌 경우 활성 상태로 간주
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.SUSPENDED; // 활동 정지 상태가 아닌 경우 활성 상태로 간주
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 처리를 여기서 할 수 있음
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.APPROVED; // 승인된 사용자만 활성화된 것으로 간주
    }

    public static UserDetails from(UserResponse user) {
        Set<GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRoleName()))
                .collect(Collectors.toSet());

        AuthUserDetails authUserDetails = AuthUserDetails.builder()
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

