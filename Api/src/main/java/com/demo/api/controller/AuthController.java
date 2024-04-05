package com.demo.api.controller;

import com.demo.api.service.AuthService;
import com.demo.modules.dto.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissueToken")
    public ResponseEntity<JwtTokenDto> reissueToken(@CookieValue("AccessToken") String accessToken) {
        JwtTokenDto tokenDto = authService.reissueToken(accessToken);
        return ResponseEntity.ok(tokenDto);
    }

}
