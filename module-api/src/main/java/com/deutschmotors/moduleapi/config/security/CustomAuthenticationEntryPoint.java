package com.deutschmotors.moduleapi.config.security;

import com.deutschmotors.modulecommon.apispec.Api;
import com.deutschmotors.modulecommon.apispec.Result;
import com.deutschmotors.modulecommon.error.AuthErrorCode;
import com.deutschmotors.modulecommon.utils.JacksonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        AuthErrorCode exceptionErrorCode = (AuthErrorCode)request.getAttribute("exception");

        Integer errorCode = exceptionErrorCode != null ? exceptionErrorCode.getErrorCode() : 500;
        String errorMessage = exceptionErrorCode != null ? exceptionErrorCode.getDescription() : "서버에러";
        String description = exceptionErrorCode == null ? exception.getMessage() : "";

        Result result = Result.builder()
                .resultCode(errorCode)
                .resultMessage(errorMessage)
                .resultDescription(description)
                .build();

        String responseBody = JacksonUtils.toJsonString(Api.ERROR(result));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(Objects.requireNonNull(responseBody));
    }
}
