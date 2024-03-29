package com.demo.api.config;


import com.demo.api.security.handler.OAuth2AuthenticationFailHandler;
import com.demo.api.security.handler.OAuth2AuthenticationSuccessHandler;
import com.demo.modules.enums.CustomOAuth2Provider;
import com.demo.api.security.anotation.CorsProperties;
import com.demo.api.security.anotation.GoogleOAuth2Properties;
import com.demo.api.security.service.CustomOAuth2UserService;
import com.demo.api.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.NimbusAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // OAuth 2.0 로그인 등록
@Slf4j
public class SecurityConfig {

// Authorization Code with PKCE 이 방법으로 구현하도록 하자!

    private final CorsProperties corsProperties;

    private final Environment env;

    private final List<String> clients = Arrays.asList("kakao", "naver", "google");

    private final CustomOAuth2UserService auth2UserService;

    // 구글 스코프
    private final GoogleOAuth2Properties googleScopes;

//    private final OAuth2AuthenticationSuccessHandler auth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailHandler auth2AuthenticationFailHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService(); // CustomUserDetailsService는 UserDetailsService 인터페이스를 구현한 클래스입니다.
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/oauth/**", "/login/**", "/images/**", "/favicon.ico/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(
                        oauth2 -> oauth2
                                .loginPage("/oauth/login")
                                .clientRegistrationRepository(clientRegistrationRepository())
                                .authorizedClientRepository(authorizedClientRepository())
                                .authorizedClientService(auth2AuthorizedClientService())
//                               카카오에서 설정한 rediection 으로 생각하면 됨 / 돌아오는 위치
                                .redirectionEndpoint(
                                        redirection ->
                                                redirection
                                                        .baseUri("/login/social/**")
                                ).tokenEndpoint(tokenEndpoint -> tokenEndpoint // 토큰 엔드포인트 설정
                                        .accessTokenResponseClient(accessTokenResponseClient()))
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(auth2UserService)
                                )
                                .successHandler( // // 인증 성공 시 Handler
                                        new OAuth2AuthenticationSuccessHandler(auth2AuthorizedClientService())
                                )
                                .failureHandler( // 인증 실패 시 Handler
                                        auth2AuthenticationFailHandler
                                )
//                                .defaultSuccessUrl("/")

//                              .failureUrl("/auth/error");
                );
        return http.build();
    }


    //                .oauth2Client(oauth2Client -> oauth2Client
//                                .clientRegistrationRepository(clientRegistrationRepository())
////                        authorizedClientRepository   사용자가 인증된 클라이언트 정보를 저장하는 역할
//                                .authorizedClientRepository(authorizedClientRepository())
////                        리소스 자격을 부여하는 소유자와 authorized client 를 연결하는 목적 제공
//                                .authorizedClientService(oauth2AuthorizedClientService())
//                ).
//                .addFilterBefore(customOAuth2LoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);


    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        return new NimbusAuthorizationCodeTokenResponseClient();
    }

    /*
     * ClientRegistration 클라이언트 등록 정보 저장
     *
     * 인가 서버에 일차적으로 클라이언트 등록 정보의 일부를 검색하는 기능 제공
     * 다른 웹 요청이 와도 OAuth2AuthorizedClient 유지 !
     * */
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(c -> getRegistration(c))
                .filter(registration -> registration != null)
                .collect(Collectors.toList());
        return new InMemoryClientRegistrationRepository(registrations);
    }

    //    Auth2AuthorizedClient 의 저장,조회, 삭제 관리
    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    //    //     OAuth2AuthorizedClient 정보를 계속 유지
    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(auth2AuthorizedClientService());
    }

    private static String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";

    private ClientRegistration getRegistration(String client) {
        String clientId = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".client-id");

        if (clientId == null) {
            return null;
        }

        // API Client Secret 불러오기
        String clientSecret = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".client-secret");

        // Redirect URI 설정
//        String redirectUri = env.getProperty(CLIENT_PROPERTY_KEY + client + ".redirect-uri");

        if ("google".equals(client)) {
            return CustomOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientSecret(clientSecret)
                    .clientId(clientId)
                    .scope(googleScopes.getScope())
                    .build();
        }

        if ("naver".equals(client)) {
            return CustomOAuth2Provider.NAVER.getBuilder(client)
                    .clientSecret(clientSecret)
                    .clientId(clientId)
                    .build();
        }

        if ("kakao".equals(client)) {
            return CustomOAuth2Provider.KAKAO.getBuilder(client)
                    .clientSecret(clientSecret)
                    .clientId(clientId)
                    .build();
        }

        return null;
    }


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


}
