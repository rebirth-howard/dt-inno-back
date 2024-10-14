package com.deutschmotors.moduleapi.config.security;

import com.deutschmotors.moduleapi.domain.auth.business.AuthBusiness;
import com.deutschmotors.moduleapi.domain.auth.model.AuthAuthenticationToken;
import com.deutschmotors.moduleapi.domain.auth.model.AuthUserDetails;
import com.deutschmotors.modulecommon.error.AuthErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AuthBusiness authBusiness;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof AuthAuthenticationToken)) {
            throw new ApiException(AuthErrorCode.AUTH_EXCEPTION, "Only CustomAuthenticationToken is supported");
        }

        AuthAuthenticationToken customAuth = (AuthAuthenticationToken) authentication;
        String username = customAuth.getName();
        String password = (String) customAuth.getCredentials();

        AuthUserDetails user = authBusiness.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApiException(AuthErrorCode.ILLEGAL_ARGUMENT, "Invalid username or password");
        }

        return AuthAuthenticationToken.builder()
                .principal(user)
                .credentials(null)
                .authorities(user.getUserRoles())
                .authenticated(true)
                .build();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
