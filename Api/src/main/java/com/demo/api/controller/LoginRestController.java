package com.demo.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class LoginRestController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    private final ClientRegistrationRepository clientRegistrationRepository;

//    private final DefaultOAuth2AuthorizedClientManager authorizedClientManager;

    /*
     * 인증된 서버에 등록해주는 리디렉션 URI
     *   스프링 시큐리티에서 인증 실패(예외)가 발생하면 사용자 웹브라우저에 인증서버 로그인 페이지로 리다이렉트
     *  다시 인증서버가 클라이언트로 Authorization Code를 넘겨줄 때 사용하는 URL
     *
     *  여기에서 accessToken 을 받아서 사용자 정보를 요청해야함!!
     * */
    @GetMapping("/social/{provider}")
    public String login(@PathVariable String provider, @RequestParam String code, HttpServletResponse response) throws IOException {

        System.out.println("provider ??? " + provider);
        // 접근 토큰 얻어옴
        System.out.println("code ??? " + code);

        //
        return null;
    }


}
