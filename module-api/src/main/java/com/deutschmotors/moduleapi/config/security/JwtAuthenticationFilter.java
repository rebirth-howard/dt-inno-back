package com.deutschmotors.moduleapi.config.security;

import com.deutschmotors.moduleapi.domain.auth.business.AuthBusiness;
import com.deutschmotors.moduleapi.domain.auth.model.AuthAuthenticationToken;
import com.deutschmotors.moduleapi.domain.auth.model.AuthUserDetails;
import com.deutschmotors.modulecommon.error.AuthErrorCode;
import com.deutschmotors.modulecommon.error.UserErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
import com.deutschmotors.modulecommon.jwt.TokenHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.deutschmotors.moduleapi.config.security.SecurityConfig.AUTH_WHITE_LIST;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthBusiness authBusiness;
    private final TokenHelper tokenHelper;

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
                String username = tokenHelper.extractUsername(jwt);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                // 사용자 ID가 존재하고 현재 SecurityContext에 인증이 없는 경우 처리
                if (username != null && authentication == null) {
                    // 사용자 정보 로드
                    AuthUserDetails authUserDetails = (AuthUserDetails) authBusiness.loadUserByUsername(username);

                    // 토큰의 유효성 검사
                    if (tokenHelper.validateToken(jwt, authUserDetails.toCommonUserDetails())) {
                        // 인증 객체 생성 및 SecurityContext 설정
                        AuthAuthenticationToken.AuthAuthenticationTokenBuilder authTokenBuilder = AuthAuthenticationToken.builder()
                                .principal(authUserDetails)
                                .credentials(null)
                                .authorities(authUserDetails.getUserRoles())
                                .authenticated(true);

                        // 토큰에서 Role 정보를 추출하고 사용자 정보와 비교
                        List<String> role = tokenHelper.extractUserRoles(jwt);


                        if(ObjectUtils.isEmpty(authUserDetails.getUserRoles())){
                            // 최소 Role(임시회원)이 존재하지 않으면 오류
                            throw new ApiException(UserErrorCode.USER_ACCESS_FORBIDDEN);
                        }

                        // TODO: ROLE 별 제어 필요시 추가 필요(role 갱신에 대한 예외처리 등)

                        // 요청에 대한 인증 세부 정보 설정
                        AuthAuthenticationToken authToken = authTokenBuilder.details(new WebAuthenticationDetailsSource().buildDetails(request)).build();
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        request.setAttribute("requestTime", LocalDateTime.now());
                    } else {
                        throw new ApiException(AuthErrorCode.EXPIRED_TOKEN);
                    }
                } else {
                    throw new ApiException(AuthErrorCode.INVALID_TOKEN);
                }
            } catch (ApiException e) {
                // 보안 관련 예외 및 JWT 형식 오류 처리
                String message = e.getErrorDescription();

                switch (message) {
                    case "유효하지 않은 토큰" -> request.setAttribute("exception", AuthErrorCode.INVALID_TOKEN);
                    case "잘못된 유형의 토큰" -> request.setAttribute("exception", AuthErrorCode.WRONG_TYPE_TOKEN);
                    case "만료된 토큰" -> request.setAttribute("exception", AuthErrorCode.EXPIRED_TOKEN);
                    case "토큰 알수없는 에러" -> request.setAttribute("exception", AuthErrorCode.TOKEN_EXCEPTION);
                    case "유효한 회원이 아닙니다." -> request.setAttribute("exception", AuthErrorCode.USER_ACCESS_FORBIDDEN);
                    case "알수없는 인증 에러" -> request.setAttribute("exception", AuthErrorCode.AUTH_EXCEPTION);
                    default -> request.setAttribute("exception", AuthErrorCode.AUTH_EXCEPTION);
                }
            } catch (Exception e) {
                request.setAttribute("exception", AuthErrorCode.AUTH_EXCEPTION);
                log.error("=== JwtAuthenticationFilter Exception === >>>>>> \n{} ", e.getMessage(), e);
            }

        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);

    }
}
