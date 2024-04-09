package com.demo.api.config;

import com.demo.api.security.anotation.GoogleOAuth2Properties;
import com.demo.api.security.filter.JwtExceptionFilter;
import com.demo.api.security.filter.JwtTokenFilter;
import com.demo.api.security.handler.OAuth2AuthenticationFailHandler;
import com.demo.api.security.handler.OAuth2AuthenticationSuccessHandler;
import com.demo.api.security.provider.JwtTokenProvider;
import com.demo.api.security.service.CustomOAuth2UserService;
import com.demo.modules.enums.CustomOAuth2Provider;
import com.demo.modules.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {

    private final Environment env;
    private final List<String> clients = Arrays.asList("kakao", "naver", "google");

    private final CustomOAuth2UserService auth2UserService;

    // 구글 스코프
    private final GoogleOAuth2Properties googleScopes;

    private final OAuth2AuthenticationFailHandler auth2AuthenticationFailHandler;

    private final JwtTokenFilter jwtTokenFilter;

    private final JwtExceptionFilter jwtExceptionFilter;

    private final TokenRepository tokenRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/oauth/**", "/login/**", "/images/**").permitAll()
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
                                                        .baseUri("/login/social/**"))
                                .tokenEndpoint(tokenEndpoint -> tokenEndpoint // 토큰 엔드포인트 설정
                                        .accessTokenResponseClient(accessTokenResponseClient()))
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(auth2UserService)
                                )
                                .successHandler( // 인증 성공 시 Handler
                                        new OAuth2AuthenticationSuccessHandler(auth2AuthorizedClientService(), jwtTokenProvider, tokenRepository)
                                )
                                .failureHandler( // 인증 실패 시 Handler
                                        auth2AuthenticationFailHandler
                                )
                );

        http.cors();

        /* jwt 사용한다고 가정 시 */
        http.csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(
                jwtTokenFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        /* 필터에서 에러 핸들링 하기위해 필터 추가 */
        http.addFilterBefore(
                jwtExceptionFilter,
                jwtTokenFilter.getClass()
        );

        return http.build();
    }


    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        return new DefaultAuthorizationCodeTokenResponseClient();
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

    //     리소스 자격을 부여하는 소유자와 authorized client 를 연결하는 목적 제공
    //    Auth2AuthorizedClient 의 저장,조회, 삭제 관리
    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    //     OAuth2AuthorizedClient 정보를 계속 유지
    //    사용자가 인증된 클라이언트 정보를 저장하는 역할
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

}
