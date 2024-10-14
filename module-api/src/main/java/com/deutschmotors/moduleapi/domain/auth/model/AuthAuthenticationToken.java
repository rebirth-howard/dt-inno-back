package com.deutschmotors.moduleapi.domain.auth.model;

import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthAuthenticationToken implements Authentication {

    // 추가 인증 정보 필요 한 경우 사용(예, 역할의 권한 등)
    private AuthUserDetails principal;
    private String credentials;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean authenticated;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return principal == null ? new HashSet<>() : principal.getUserRoles();
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal == null ? null : principal.getLoginId();
    }

}
