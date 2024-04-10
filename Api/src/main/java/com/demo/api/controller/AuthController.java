package com.demo.api.controller;

import com.demo.api.security.provider.JwtTokenProvider;
import com.demo.api.service.AuthService;
import com.demo.api.service.MemberService;
import com.demo.modules.dto.JwtTokenDto;
import com.demo.modules.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    private final MemberService memberService;

    @PostMapping("/reissueToken")
    public ResponseEntity<JwtTokenDto> reissueToken(@CookieValue("RefreshToken") String refreshToken) {
        JwtTokenDto tokenDto = authService.reissueToken(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokenDto.getAccessToken());
        return ResponseEntity.ok().headers(headers).body(tokenDto);
    }

    @GetMapping("/user")
    public ResponseEntity<MemberDto> getUserInfo(Authentication authentication) {
        MemberDto member = memberService.getMember(authentication.getName());
        return ResponseEntity.ok().body(member);
    }

    @GetMapping("/accessToken")
    public ResponseEntity<JwtTokenDto> getJwtToken(@CookieValue("AccessToken") String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        return ResponseEntity.ok().body(JwtTokenDto
                .builder()
                .accessToken(accessToken)
                .build());
    }

//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//        //1. Request Header 에서 JWT Token 추출
//        String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);
//
//        //2. validateToken 메서드로 토큰 유효성 검사
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//            if (!((HttpServletRequest) servletRequest).getRequestURI().equals("/v1/user/reissue")) {
//                Authentication authentication = jwtTokenProvider.getAuthentication(token);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//        filterChain.doFilter(servletRequest, servletResponse);
//    }

}
