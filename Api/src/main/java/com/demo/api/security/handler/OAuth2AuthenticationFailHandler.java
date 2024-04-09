package com.demo.api.security.handler;

import com.demo.api.security.provider.JwtTokenProvider;
import com.demo.modules.error.CustomException;
import com.demo.modules.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationFailHandler implements AuthenticationFailureHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println("======= OAuth2AuthenticationFailHandler onAuthenticationFailure=========");
        log.debug("============== request ==============");
        log.debug("{}" , request);
        log.debug("============== response ==============");
        log.debug("{}" , response);
        jwtTokenProvider.removeCookie("AccessToken");
        jwtTokenProvider.removeCookie("RefreshToken");
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            // AJAX 요청인 경우
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + exception.getMessage());
        } else {
            // OAuth2 요청 또는 일반적인 요청인 경우
            response.sendRedirect("http://localhost:3000");
        }
    }

}