package com.demo.api.security.filter;

import com.demo.api.security.provider.JwtTokenProvider;
import com.demo.api.service.AuthService;
import com.demo.modules.enums.TokenStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
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
    private final List<String> excludedPatterns = Arrays.asList("/oauth/**", "/login/**", "/images/**" ,"/favicon.*", "/*/icon-*");

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        Cookie[] cookies = request.getCookies();

        /* 제외할 url 처리 */if (shouldNotFilter(request)) {
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
            response.sendRedirect("/oauth/login");
            return;
        }

        /* 액세스 토큰 유효성 검사 */
        TokenStatus tokenStatus = jwtTokenProvider.validateToken(accessToken);

        switch (tokenStatus) {
            case VALID:
                filterChain.doFilter(request, response);
                break;
//        액세스 토큰이 만료된 경우
            case EXPIRED:
                String refreshToken = Optional.ofNullable(cookies)
                        .stream()
                        .flatMap(Arrays::stream)
                        .filter(cookie -> "RefreshToken".equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null);
                /* 쿠키에서 리프레시 토큰 확인 */
                if (refreshToken == null) {
                    response.sendRedirect("/oauth/login");
                    return;
                }
                /* 리프레시 토큰 재발행 시도 */
                authService.reissueToken(refreshToken);
                break;
            case INVALID:
                response.sendRedirect("/oauth/login");
                break;
        }

        /* 리프레시 토큰으로 액세스 토큰 재발행  */

        /* 새 액세스 토큰으로 요청 재설정 */
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return excludedPatterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
