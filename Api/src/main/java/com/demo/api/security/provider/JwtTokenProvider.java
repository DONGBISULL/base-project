package com.demo.api.security.provider;


import com.demo.modules.dto.CustomUserDetail;
import com.demo.modules.dto.JwtTokenDto;
import com.demo.modules.dto.MemebrDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("${app.name}")
    private String appName;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-token.expire-time}")
    private Long refreshTokenExpireTime;

    @Value("${jwt.access-token.expire-time}")
    private Long accessTokenExpireTime;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    private SecretKey key;

    @PostConstruct
    private void initialize() {
        getSigningKey();
    }

    private void getSigningKey() {
//        HMAC 알고리즘을 기반으로 비밀 키를 생성
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    /** 처음 로그인했을 경우 토큰 생성 */
    public JwtTokenDto createToken(Authentication authentication) {
        CustomUserDetail principal = (CustomUserDetail) authentication.getPrincipal();
        System.out.println(principal);
        Map<String, Object> claims = new HashMap<>();

        MemebrDto member = principal.getMember();
        claims.put("username", principal.getUsername());
        claims.put("email", member.getEmail());
        claims.put("socialId", member.getSocialId());
        claims.put("providerType", member.getProviderType());
        claims.put("imageUrl", member.getImageUrl());

        long now = (new Date()).getTime();
        if (authentication instanceof OAuth2AuthenticationToken) {
            String accessToken = createAccessToken(authentication, claims, now);
            String refreshToken = createRefreshToken(now);
            return JwtTokenDto.builder()
                    .memberId(principal.getUsername())
                    .refreshToken(refreshToken)
                    .accessToken(accessToken)
                    .refreshExpirationDate(new Date(now + refreshTokenExpireTime))
                    .build();
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {

        }
        return null;
    }



    /**
     * 리프레시 토큰 생성
     */
    private String createRefreshToken(long now) {
        Date refreshTokenExpiresTime = new Date(now + refreshTokenExpireTime);
        String refreshToken = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .expiration(refreshTokenExpiresTime)
                .signWith(key)
                .compact();
        return refreshToken;
    }

    /**
     * 액세스 토큰 생성
     */
    private String createAccessToken(Authentication authentication, Map<String, Object> claims, long now) {
        Date accessTokenExpiresTime = new Date(now + accessTokenExpireTime);
        log.debug(authentication.toString());
        String accessToken = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .issuer(appName)
                .subject(authentication.getName())
                .expiration(accessTokenExpiresTime)
                .claims(claims)
                .signWith(key)
                .compact();
        return accessToken;
    }


    public Boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = getJwtClaims(token);
            Claims payload = claimsJws.getPayload();
            log.info("============ payload =================");
            log.info(payload.toString());
            String subject = payload.getSubject();
            log.info("============ subject =================");
            log.info(subject);
            log.info("============ Expiration =================");
            log.info(payload.getExpiration().toString());
            log.info("============ notBefore =================");
            /* 현재 시간 */
            Date now = new Date();
            Date expiration = payload.getExpiration();
            Date notBefore = Optional.ofNullable(payload.getNotBefore()).orElse(now);
            if (now.after(notBefore) && now.before(expiration)) {
                return true;
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            log.debug("jwt 문자열이 null 이거나 비어있음 ");
        } catch (JwtException e) {
            log.debug("나 유효성을 검증할 수 없는 경우");
        }
        return false;
    }

    public Jws<Claims> getJwtClaims(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(key) // 사용된 키 설정
                .build();
        return parser.parseSignedClaims(token);
    }

    public Claims getJwtClaimsByPayload(String token) {
        Jws<Claims> jwtClaims = getJwtClaims(token);
        return jwtClaims.getPayload();
    }

    /**
     * 토큰의 회원 id 조회
     */
    public String getJwtCliamByMemberId(String accessToken) {
        return getJwtClaimsByPayload(accessToken).getSubject();
    }

    /**
     * 토큰에 지정한 필요한 정보 명을 통해 내용 조회
     */
    public Object getJwtCliamByName(String name, String token) {
        Claims payload = getJwtClaimsByPayload(token);
        return payload.get(name);
    }

    public JwtTokenDto reCreateAccessToken(String refreshToken) {
        return null;
    }


    public void refreshTokenWithCookie(JwtTokenDto tokenDto) {
        String refreshToken = tokenDto.getRefreshToken();
        Cookie cookie = new Cookie("RefreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setSecure(true);
        Date now = new Date();
        long maxTime = now.getTime() - tokenDto.getRefreshExpirationDate().getTime();
        cookie.setMaxAge((int)  maxTime);
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getResponse();
        response.addCookie(cookie);
    }

//    public void setRefreshTokenAtCookie(RefreshToken refreshToken) {
//        Cookie cookie = new Cookie("refreshToken", refreshToken.getRefreshToken());
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        cookie.setMaxAge(refreshToken.getExpiration().intValue());
//        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
//                .getRequestAttributes()).getResponse();
//        response.addCookie(cookie);
//    }


}
