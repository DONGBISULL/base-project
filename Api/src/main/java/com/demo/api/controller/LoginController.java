package com.demo.api.controller;

import com.demo.api.security.anotation.GoogleOAuth2Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final GoogleOAuth2Properties googleScopes;
    @GetMapping("/oauth/login")
    public String loginPage(Model model) {
        model.addAttribute("googleScopes", googleScopes.getScope());
        return "oauth_login";
    }
}
