package com.demo.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Configuration
public class JpaAuditorConfig implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        String loginId = "ANONYMOUS";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() )  return Optional.of(loginId);
        Object principal = authentication.getPrincipal();
        if (principal != null && !principal.equals("anonymousUser")) {
            UserDetails userDetails = (UserDetails) principal;
            loginId = userDetails.getUsername(); // 로그인 ID를 사용자명으로 설정
        }
        if (ObjectUtils.isEmpty(loginId)) {
            return Optional.empty();
        } else {
            return Optional.of(loginId);
        }
    }
}
