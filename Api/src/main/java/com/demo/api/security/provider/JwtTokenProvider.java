package com.demo.api.security.provider;


import com.demo.modules.dto.CustomUserDetail;
import com.demo.modules.dto.JwtTokenDto;
import com.demo.modules.dto.MemberDto;
import com.demo.modules.dto.MemberUser;
import com.demo.modules.entity.Member;
import com.demo.modules.enums.ProviderType;
import com.demo.modules.enums.Role;
import com.demo.modules.enums.TokenStatus;
import com.demo.modules.error.CustomException;
import com.demo.modules.error.ErrorCode;
import com.nimbusds.jwt.JWT;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

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

    private final ModelMapper modelMapper;

    @PostConstruct
    private void initialize() {
        getSigningKey();
    }

    private void getSigningKey() {
//        HMAC 알고리즘을 기반으로 비밀 키를 생성
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * AccessToken 헤더에 세팅
     */
    public void setAuthorizeHeader(HttpServletResponse response, String token) {
        response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + token);
    }

    /**
     * AccessToken 헤더에서 얻기
     */
    public String getAccessTokenByHeader(String authorizationHeader) {
        String accessToken = authorizationHeader.substring(TOKEN_PREFIX.length());
        return accessToken;
    }

    public MemberDto getMemberDto(Claims payload) {
        String memId = payload.getSubject();
        String username = (String) payload.get("username");
        String email = (String) payload.get("getEmail");
        String socialId = (String) payload.get("socialId");
        String providerName = (String) payload.get("providerType");
        ProviderType providerType = ProviderType.valueOf(providerName);
        String imageUrl = (String) payload.get("imageUrl");
        return MemberDto.builder()
                .build();
    }

    /**
     * JWT 토큰에 들어있는 정보fh Authentication 생성
     */
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims payload = getJwtClaimsByPayload(accessToken);

        MemberDto memberDto = getMemberDto(payload);
        Set<Role> roles = (Set<Role>) payload.get("roles");
        memberDto.setRoles(roles);
        Set<GrantedAuthority> authorities = getAuthorities(roles);

        Member member = modelMapper.map(memberDto, Member.class);

        CustomUserDetail userDetail = new CustomUserDetail(
                new MemberUser(
                        member, UUID.randomUUID().toString(), authorities), null);
        // UserDetails 객체를 만들어서 Authentication 리턴
//        UserDetails principal = new User(claims.getSubject(), "", authorities);
//        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        /* UsernamePasswordAuthenticationToken
         * 주체 userDetail :사용자
         * credentials : 보통 비밀번호
         * authorities : 권한
         *  */
        return new UsernamePasswordAuthenticationToken(userDetail, "", authorities);
    }

    private Set<GrantedAuthority> getAuthorities(Set<Role> roles) {
        if (roles != null && !roles.isEmpty()) {
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.name()))
                    .collect(Collectors.toSet());
        }
        return null;
    }

    /**
     * 처음 로그인했을 경우 토큰 생성
     */
    public JwtTokenDto createToken(MemberDto memberDto) {
        long now = (new Date()).getTime();
        String accessToken = generateAccessToken(memberDto, now);
        String refreshToken = generateRefreshToken(now);
        return JwtTokenDto.builder()
                .memberId(memberDto.getId())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .accessExpirationDate(new Date(now + accessTokenExpireTime))
                .refreshExpirationDate(new Date(now + refreshTokenExpireTime))
                .build();
    }

    /* memberDto 만으로 토큰 생성하도록 구현 */
    public String generateAccessToken(MemberDto memberDto, long now) {
        Map<String, Object> claims = new HashMap<>();
        setMemberClaim(memberDto, claims);
        return createAccessToken(memberDto, claims, now);
    }

    /**
     * Claim 세팅하는 함수 분리
     */
    private void setMemberClaim(MemberDto member, Map<String, Object> claims) {
        claims.put("username", member.getId());
        claims.put("email", member.getEmail());
        claims.put("socialId", member.getSocialId());
        claims.put("providerType", member.getProviderType());
        claims.put("imageUrl", member.getImageUrl());
        claims.put("roles", member.getRoles());
    }

    /**
     * 리프레시 토큰 생성
     */
    private String generateRefreshToken(long now) {
        Date refreshTokenExpiresTime = new Date(now + refreshTokenExpireTime);
        String refreshToken = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .notBefore(new Date(now))
                .expiration(refreshTokenExpiresTime)
                .signWith(key)
                .compact();
        return refreshToken;
    }

    /**
     * 액세스 토큰 생성
     */
    private String createAccessToken(MemberDto memberDto, Map<String, Object> claims, long now) {
        Date accessTokenExpiresTime = new Date(now + accessTokenExpireTime);
        log.debug(memberDto.toString());
        String accessToken = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .issuer(appName)
                .notBefore(new Date(now))
                .subject(memberDto.getId())
                .expiration(accessTokenExpiresTime)
                .claims(claims)
                .signWith(key)
                .compact();
        return accessToken;
    }


    public TokenStatus validateToken(String token) {
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
                return TokenStatus.VALID;
            } else {
                return TokenStatus.EXPIRED;
            }
        } catch (IllegalArgumentException e) {
            log.debug("jwt 문자열이 null 이거나 비어있음 ");
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST);
        } catch (JwtException e) {
            if (e instanceof ExpiredJwtException) {
                log.debug("토큰이 만료되었습니다.");
                return TokenStatus.EXPIRED;
            } else {
                log.debug("유효성을 검증할 수 없는 경우");
                throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
            }
        }
//        return TokenStatus.INVALID;
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

    public void refreshTokenWithCookie(JwtTokenDto tokenDto) {
        setCookie(tokenDto.getRefreshToken(), "RefreshToken", tokenDto.getRefreshExpirationDate());
    }

    public void accessTokenWithCookie(JwtTokenDto tokenDto) {
        setCookie(tokenDto.getAccessToken(), "AccessToken", tokenDto.getAccessExpirationDate());
    }

    public void setCookie(String token, String tokenType, Date expireTime) {
        String accessToken = token;
        Cookie cookie = new Cookie(tokenType, accessToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        Date now = new Date();
        long maxTime = expireTime.getTime() - now.getTime();
        cookie.setMaxAge((int) maxTime / 1000);
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getResponse();
        response.addCookie(cookie);
    }

    /**
     * 토큰 삭제
     */
    public void removeCookie(String token, String tokenType) {
        String accessToken = token;
        Cookie cookie = new Cookie(tokenType, accessToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getResponse();
        response.addCookie(cookie);
    }
}
