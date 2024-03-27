package com.demo.api.controller;

import com.demo.api.security.anotation.GoogleOAuth2Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final OAuth2AuthorizedClientService authorizedClientService;


    @GetMapping("/")
    public String index(HttpSession session, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/oauth/login";
        }
//        ClientRegistration kakaoRegistration = clientRegistrationRepository.findByRegistrationId("kakao");

        return "index";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        model.addAttribute("oAuth2AuthenticationToken", authentication);
        return "home";
    }
}
