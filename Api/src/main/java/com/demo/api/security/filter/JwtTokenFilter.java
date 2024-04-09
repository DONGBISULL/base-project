package com.demo.api.security.filter;

import com.demo.api.security.provider.JwtTokenProvider;
import com.demo.api.service.AuthService;
import com.demo.modules.enums.TokenStatus;
import com.demo.modules.error.CustomException;
import com.demo.modules.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    /* 제외할 url */
    private final List<String> excludedPatterns = Arrays.asList("/oauth/**", "/login/**", "/images/**", "/favicon.*", "/*/icon-*");

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthService authService;

    @Value("${front.api}")
    private String frontApi;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null) {

            Cookie[] cookies = request.getCookies();

            /* 제외할 url 처리 */
            if (shouldNotFilter(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            /*  요청에서 액세스 토큰 확인 */
            String accessToken = Optional.ofNullable(cookies)
                    .stream()
                    .flatMap(Arrays::stream)
                    .filter(cookie -> "AccessToken".equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if (accessToken == null) {
//            response.sendRedirect("/oauth/login");
                throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_ACCESS_TOKEN);
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "access token 없음");
            }

            /* 액세스 토큰 유효성 검사 */
            TokenStatus tokenStatus = jwtTokenProvider.validateToken(accessToken);

            switch (tokenStatus) {
                case VALID:
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    break;
//        액세스 토큰이 만료된 경우
                case EXPIRED:
//                    String refreshToken = Optional.ofNullable(cookies)
//                            .stream()
//                            .flatMap(Arrays::stream)
//                            .filter(cookie -> "RefreshToken".equals(cookie.getName()))
//                            .findFirst()
//                            .map(Cookie::getValue)
//                            .orElse(null);
                    /* 에러를 발생시켜서 재요청 보내도록 수정 */
                    throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.EXPIRED_ACCESS_TOKEN);
                    /* 리프레시 토큰 재발행 시도 */
//                    authService.reissueToken(refreshToken);
                case INVALID:
                    throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_ACCESS_TOKEN);
//                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "access token 유효하지 않음 ");
//                response.sendRedirect("/oauth/login");
//                    break;
            }

        }

        /* 새 액세스 토큰으로 요청 재설정 */
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return excludedPatterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
