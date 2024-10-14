package com.deutschmotors.moduleapi.config.security;

import com.deutschmotors.modulecommon.apispec.Api;
import com.deutschmotors.modulecommon.apispec.Result;
import com.deutschmotors.modulecommon.error.ErrorCode;
import com.deutschmotors.modulecommon.utils.JacksonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Result result = Result.builder()
                .resultCode(ErrorCode.SERVER_ERROR.getErrorCode())
                .resultMessage(ErrorCode.SERVER_ERROR.getDescription())
                .resultDescription(accessDeniedException.getMessage())
                .build();

        String responseBody = JacksonUtils.toJsonString(Api.ERROR(result));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
