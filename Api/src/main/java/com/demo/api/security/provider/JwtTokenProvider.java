package com.demo.api.security.provider;


import com.demo.common.utils.DateUtils;
import com.demo.modules.dto.CustomUserDetail;
import com.demo.modules.dto.JwtTokenDto;
import com.demo.modules.dto.MemberDto;
import com.demo.modules.dto.MemberUser;
import com.demo.modules.entity.Member;
import com.demo.modules.enums.Role;
import com.demo.modules.enums.TokenStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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

    private final ModelMapper modelMapper;
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

    /**
     * 처음 로그인했을 경우 토큰 생성
     */
    public JwtTokenDto createToken(MemberDto memberDto) {
        LocalDateTime now = LocalDateTime.now();
        String accessToken = generateAccessToken(memberDto, now);
        String refreshToken = generateRefreshToken(memberDto, now);
        LocalDateTime accessExpirationDate = now.plus(Duration.ofMillis(accessTokenExpireTime));
        LocalDateTime refreshExpirationDate = now.plus(Duration.ofMillis(refreshTokenExpireTime));
        return JwtTokenDto.builder()
                .memberId(memberDto.getId())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .accessExpirationDate(accessExpirationDate)
                .refreshExpirationDate(refreshExpirationDate)
                .build();
    }

    /* memberDto 만으로 토큰 생성하도록 구현 */
    public String generateAccessToken(MemberDto memberDto, LocalDateTime now) {
        Map<String, Object> claims = new HashMap<>();
        setMemberClaim(memberDto, claims);
        return createAccessToken(memberDto, claims, now);
    }

    /**
     * 리프레시 토큰 생성
     */
    private String generateRefreshToken(MemberDto memberDto, LocalDateTime now) {
        LocalDateTime refreshExpirationDate = now.plus(Duration.ofMillis(refreshTokenExpireTime));
        Date nowDate = DateUtils.toDate(now);
        Date refreshDate = DateUtils.toDate(refreshExpirationDate);
        String refreshToken = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .notBefore(nowDate)
                .subject(memberDto.getId())
                .expiration(refreshDate)
                .signWith(key)
                .compact();
        return refreshToken;
    }


    /**
     * Claim 세팅하는 함수 분리
     */
    private void setMemberClaim(MemberDto member, Map<String, Object> claims) {
        claims.put("username", member.getId());
        claims.put("email", member.getEmail());
        claims.put("socialId", member.getSocialId());
//        claims.put("providerType", member.getProviderType());
//        claims.put("imageUrl", member.getImageUrl());
//        claims.put("roles", member.getRoles());
    }


    /**
     * 액세스 토큰 생성
     */
    private String createAccessToken(MemberDto memberDto, Map<String, Object> claims, LocalDateTime now) {
        LocalDateTime accessExpireDateTime = now.plus(Duration.ofMillis(accessTokenExpireTime));
        Date nowDate = DateUtils.toDate(now);
        Date expireDate = DateUtils.toDate(accessExpireDateTime);
        log.debug("createAccessToken expireDate {} " , expireDate);
        String accessToken = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .issuer(appName)
                .notBefore(nowDate)
                .subject(memberDto.getId())
                .expiration(expireDate)
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
//            Date now = new Date();
//            Date expiration = payload.getExpiration();
//            Date notBefore = Optional.ofNullable(payload.getNotBefore()).orElse(now);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiration = DateUtils.toLocalDateTime(payload.getExpiration());
            LocalDateTime notBefore = Optional.ofNullable(payload.getNotBefore())
                    .map(Date::toInstant)
                    .map(instant -> instant.atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .orElse(now);
            if (now.isAfter(notBefore) && now.isBefore(expiration)) {
                return TokenStatus.VALID;
            } else {
                return TokenStatus.EXPIRED;
            }
        } catch (IllegalArgumentException e) {
            log.debug("jwt 문자열이 null 이거나 비어있음 ");
        } catch (JwtException e) {
            if (e instanceof ExpiredJwtException) {
                log.debug("토큰이 만료되었습니다.");
                return TokenStatus.EXPIRED;
            } else {
                log.debug("나 유효성을 검증할 수 없는 경우");
            }
        }
        return TokenStatus.INVALID;
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

    /* 토큰으로 Authentication 생성 */
    public Authentication getAuthentication(String token) {
        MemberDto memberDto = createMemberDtoFromJwtToken(token);
        return createAuthentication(memberDto);
    }

    public MemberDto createMemberDtoFromJwtToken(String token) {
        Claims payload = getJwtClaimsByPayload(token);
        String username = (String) payload.get("username");
        String email = (String) payload.get("email");
//        ProviderType providerType = ProviderType.valueOf((String) payload.get("providerType"));
        String socialId = (String) payload.get("socialId");
//        Set<Role> roles = (Set<Role>) payload.get("roles");
        return MemberDto
                .builder()
                .id(username)
                .email(email)
//                .providerType(providerType)
                .socialId(socialId)
//                .roles(roles)
                .build();
    }

    /* memberDTO 로 Authentication 생성 */
    public Authentication getAuthentication(MemberDto memberDto) {
        return createAuthentication(memberDto);
    }

    /* role enum 들 SimpleGrantedAuthority 로 변경  */
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) return Collections.emptyList();
        Set<GrantedAuthority> rolesSet = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
        return rolesSet;
    }

    /* 공통 UsernamePasswordAuthenticationToken 생성 로직 분리 */
    private Authentication createAuthentication(MemberDto memberDto) {
        Collection<? extends GrantedAuthority> authorities = getGrantedAuthorities(memberDto.getRoles());
        Member member = modelMapper.map(memberDto, Member.class);
        String randomPassword = UUID.randomUUID().toString();
        member.setPassword(randomPassword);
        Map<String, Object> attributes = new HashMap<>();
        MemberUser user = new MemberUser(member, randomPassword, authorities);
        CustomUserDetail userDetails = new CustomUserDetail(user, attributes);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
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

    public void setCookie(String token, String tokenType, LocalDateTime expireTime) {
        String accessToken = token;
        Cookie cookie = new Cookie(tokenType, accessToken);
        cookie.setHttpOnly(true);
        LocalDateTime now = LocalDateTime.now();
        long maxTime = ChronoUnit.SECONDS.between(now, expireTime);
        cookie.setMaxAge((int) (maxTime)); // 밀리초를 초로 변환하여 설정
        cookie.setPath("/");
        cookie.setDomain("localhost");
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getResponse();
        response.addCookie(cookie);
    }

    /* 토큰 삭제 */
    public void removeCookie(String tokenType) {
        Cookie cookie = new Cookie(tokenType, null);
        cookie.setMaxAge(0);
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getResponse();
        response.addCookie(cookie);
    }

}
