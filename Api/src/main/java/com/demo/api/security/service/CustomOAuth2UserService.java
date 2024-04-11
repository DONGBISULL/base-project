package com.demo.api.security.service;

import com.demo.api.service.MemberService;
import com.demo.modules.dto.CustomUserDetail;
import com.demo.modules.dto.MemberUser;
import com.demo.modules.dto.OAuthAttribute;
import com.demo.modules.entity.Member;
import com.demo.modules.enums.ProviderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberService memberService;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("=========== loadUser ===============");
        log.trace("Load user {}", userRequest);

//        ClientRegistration clientRegistration = userRequest.getClientRegistration();
//        log.debug("=========== clientRegistration  ===============");
//        log.debug(clientRegistration.toString());
//
//        log.debug("=========== oAuth2User  ===============");
//        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        OAuth2User oAuth2User = loadOAuth2User(userRequest);
        log.debug(oAuth2User.toString());

//        유저정보로 contirbute 생성
//        OAuthAttribute oAuthAttribute = OAuthAttribute.of(provider, userNameAttributeName, attributes);
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String registrationId = clientRegistration.getRegistrationId();
        ProviderType provider = getProvider(registrationId);

        OAuthAttribute oAuthAttribute = convertToOAuthAttribute(clientRegistration, oAuth2User, provider);
        Member member = memberService.getSocialMember(oAuthAttribute, provider);

        CustomUserDetail userDto = createCustomUserDetail(oAuth2User, member);
        log.debug("=========== userDto  ===============");
        log.debug(userDto.toString());
        return userDto;
    }

    /* user 정보 추출 함수 생성 */
    private CustomUserDetail createCustomUserDetail(OAuth2User oAuth2User, Member member) {
        Collection<? extends GrantedAuthority> authorities = extractAuthorities(member);
//        OAuthUserDto userDto = new OAuthUserDto(authorities, attributes, userNameAttributeName);
        CustomUserDetail userDto = new CustomUserDetail(new MemberUser(member, member.getPassword(), authorities), oAuth2User.getAttributes());
        return userDto;
    }

    /* 토큰 정보 로드 */
    private OAuth2User loadOAuth2User(OAuth2UserRequest userRequest) {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        log.debug("=========== clientRegistration  ===============");
        log.debug(clientRegistration.toString());

        log.debug("=========== oAuth2User  ===============");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        return delegate.loadUser(userRequest);
    }

    /* 속성으로 변환 */
    private OAuthAttribute convertToOAuthAttribute(ClientRegistration clientRegistration, OAuth2User oAuth2User, ProviderType provider) {

        ClientRegistration.ProviderDetails providerDetails = clientRegistration.getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        return OAuthAttribute.of(provider, userNameAttributeName, attributes);
    }

    /* 권한 타입 생성 */
    private Collection<? extends GrantedAuthority> extractAuthorities(Member member) {
        if (member.getRoles() == null || member.getRoles().isEmpty()) return Collections.emptyList();
        return member.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    /* 프로바이더 타입 얻기 */
    private ProviderType getProvider(String registrationId) {
        if (registrationId == null) {
            return null;
        }
        if ("kakao".equals(registrationId)) {
            return ProviderType.KAKAO;
        } else if ("naver".equals(registrationId)) {
            return ProviderType.NAVER;
        } else if ("google".equals(registrationId)) {
            return ProviderType.GOOGLE;
        } else {
            return ProviderType.NOMAL;
        }
    }
}
