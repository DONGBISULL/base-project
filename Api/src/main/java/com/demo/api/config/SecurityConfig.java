package com.demo.api.config;


import com.demo.api.security.anotation.CorsProperties;
import com.demo.api.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // OAuth 2.0 로그인 등록
@Slf4j
public class SecurityConfig {

    private final CorsProperties corsProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins()); // 모든 origin 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT")); // 모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(Arrays.asList(
                "Accept"
                , "Content-Type"
                , "Access-Control-Allow-Headers"
                , "X-Requested-With"
                , "remember-me"
                , "Authorization" // JWT
                , "Access-Control-Allow-Origin"
                , "X-CSRF-Token" //  CSRF 토큰
                , "X-Requested-With" //  AJAX 요청을 구분
                , "Connection"  //HTTP 연결 관련 정보를 포함
                , "Accept-Encoding" //  콘텐츠 인코딩
                , "X-XSRF-TOKEN" // 토큰을 전송하기 위한 헤더
                , "Cookie" // 추후 제거 가능
        )); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보를 함께 전송 허용
        // 메서드는 특정 URL 패턴에 대한 CORS 구성을 등록하는 데 사용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public UserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService(); // CustomUserDetailsService는 UserDetailsService 인터페이스를 구현한 클래스입니다.
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* 시큐리티 제외 항목 추가  */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/static/js/**", "/static/images/**", "/static/css/**", "/static/scss/**", "/favicon.*", "/*/icon-*");
    }

}
