package com.demo.api.security.handler;

import com.demo.api.security.provider.JwtTokenProvider;
import com.demo.modules.dto.CustomUserDetail;
import com.demo.modules.dto.JwtTokenDto;
import com.demo.modules.dto.MemberDto;
import com.demo.modules.entity.Member;
import com.demo.modules.entity.Token;
import com.demo.modules.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService authorizedClientService;

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenRepository tokenRepository;

    @Value("${front.api}")
    private String frontApi;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(OAuth2AuthorizedClientService authorizedClientService, JwtTokenProvider jwtTokenProvider, TokenRepository tokenRepository) {
        this.authorizedClientService = authorizedClientService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("======= OAuth2AuthenticationSuccessHandler onAuthenticationSuccess =========");
        CustomUserDetail principal = (CustomUserDetail) authentication.getPrincipal();
        MemberDto member = principal.getMember();
        JwtTokenDto token = jwtTokenProvider.createToken(member);

        /* 토큰 정보 저장 */
        tokenRepository.save(Token.builder()
                .expirationDate(token.getRefreshExpirationDate())
                .member(new Member(token.getMemberId()))
                .refreshToken(token.getRefreshToken())
                .build());

        /* 쿠키로 리프레시 토큰 생성 */
        jwtTokenProvider.accessTokenWithCookie(token);
        jwtTokenProvider.refreshTokenWithCookie(token);
        response.addHeader("Authorization", "Bearer " + token.getAccessToken());

        log.debug("login success !!!!");

        /* 프론트가 따로 분리된 상황이 아니므로 리다이렉트 처리 존재
         *   보통 헤더에 실어서 전달만 함
         * */
//        response.addHeader("Authorization", "Bearer " + accessToken);

//        response.addHeader("Refresh", refreshToken);
//        response.addHeader("location","http://localhost:3000");
//        if (authentication instanceof OAuth2AuthenticationToken) {
//            // OAuth2 소셜 로그인인 경우의 처리
//            System.out.println("=======  OAuth2AuthenticationToken =========");
////            String registrationId = oauthToken.getAuthorizedClientRegistrationId();
////            String principalName = oauthToken.getPrincipal().getName();
////            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(registrationId, principalName);
////
////            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
////            log.debug(accessToken.toString());
////            log.debug( " getTokenType " ,accessToken.getTokenType());
////            log.debug( " getScopes " ,accessToken.getScopes());
////            log.debug( " getTokenValue " ,accessToken.getTokenValue());
////            log.debug( " getExpiresAt " ,accessToken.getExpiresAt());
////            log.debug( " getIssuedAt " ,accessToken.getIssuedAt());
////
////            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
////
////            log.debug( " getExpiresAt " ,refreshToken.getExpiresAt());
////            log.debug( " getTokenValue " ,refreshToken.getTokenValue());
////            log.debug( " getIssuedAt " ,refreshToken.getIssuedAt());
////            log.debug(refreshToken.toString());
//
//        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
//            // 일반 로그인인 경우의 처리
//            System.out.println("======= UsernamePasswordAuthenticationToken =========");
//
//            // 여기에서 일반 로그인 관련 처리를 진행합니다.
//
//        }

    }

}