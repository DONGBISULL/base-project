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
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final MemberService memberService;

    private final JwtTokenProvider jwtTokenProvider;
/*
* RefreshToken
* */
    @GetMapping("/reissueToken")
    public ResponseEntity<JwtTokenDto> reissueToken(@CookieValue(name = "RefreshToken") String refreshToken) {
        log.debug("=================== reissueToken =================== ");
        log.debug("=================== refreshToken :  {} ===================", refreshToken);
        JwtTokenDto tokenDto = authService.reissueToken(refreshToken);
        return ResponseEntity.ok(tokenDto);
    }

//    @GetMapping("/user")
//    public ResponseEntity<MemberDto> authCheck(@CookieValue(value = "AccessToken", required = false) String accessToken) {
//        log.debug("=================== accessToken :  {} ===================", accessToken);
//        String memberId = jwtTokenProvider.getJwtCliamByMemberId(accessToken);
//        log.debug("=================== memberId :  {} ===================", memberId);
//        MemberDto member = memberService.getMember(memberId);
//        return ResponseEntity.ok(member);
//    }

    @PostMapping("/user")
    public ResponseEntity<MemberDto> getUserInfo(Authentication authentication) {
        MemberDto member = memberService.getMember(authentication.getName());
        return ResponseEntity.ok().body(member);
    }

    @PostMapping("/access")
    public ResponseEntity<JwtTokenDto> getJwtToken(@CookieValue("AccessToken") String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        return ResponseEntity.ok().body(JwtTokenDto
                .builder()
                .accessToken(accessToken)
                .build());
    }

}
