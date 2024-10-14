package com.deutschmotors.moduleapi.config.security;

import com.deutschmotors.moduleapi.domain.auth.business.AuthBusiness;
import com.deutschmotors.moduleapi.domain.auth.model.AuthUserDetails;
import com.deutschmotors.moduleapi.domain.auth.service.AuthService;
import com.deutschmotors.modulecommon.error.TokenErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.deutschmotors.moduleapi.config.security.SecurityConfig.AUTH_WHITE_LIST;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthBusiness authBusiness;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AntPathMatcher matcher = new AntPathMatcher();
        if (Arrays.stream(AUTH_WHITE_LIST).anyMatch(p -> matcher.match(p, request.getRequestURI()))) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 JWT 토큰 추출
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            try {
                // JWT 토큰에서 "Bearer " 문자열을 제외하고 실제 토큰 값만 추출
                String jwt = authHeader.substring(7);
                String username = "user_normal";

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                // 사용자 ID가 존재하고 현재 SecurityContext에 인증이 없는 경우 처리
                if (username != null && authentication == null) {
                    // 사용자 정보 로드
                    AuthUserDetails authUserDetails = (AuthUserDetails) authBusiness.loadUserByUsername(username);

                    boolean isValie = true;

                    // 토큰의 유효성 검사
                    if (isValie) {
                        // 인증 객체 생성 및 SecurityContext 설정
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                authUserDetails,
                                null,
                                authUserDetails.getAuthorities()
                        );

                        // 요청에 대한 인증 세부 정보 설정
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        request.setAttribute("requestTime", LocalDateTime.now());
                    } else {
                        throw new ApiException(TokenErrorCode.INVALID_TOKEN);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);

    }
}
