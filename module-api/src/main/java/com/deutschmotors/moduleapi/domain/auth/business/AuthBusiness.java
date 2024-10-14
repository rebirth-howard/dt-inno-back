package com.deutschmotors.moduleapi.domain.auth.business;

import com.deutschmotors.moduleapi.domain.auth.model.AuthUserDetails;
import com.deutschmotors.moduleapi.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthBusiness {

    private final AuthService authService;

    public AuthUserDetails loadUserByUsername(String username) {
        return authService.loadUserByUsername(username);
    }
}
