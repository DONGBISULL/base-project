package com.demo.api.security.service;

import com.demo.api.service.MemberService;
import com.demo.modules.dto.OAuthAttribute;
import com.demo.modules.dto.OAuthUserDto;
import com.demo.modules.entity.Member;
import com.demo.modules.enums.ProviderType;
import com.demo.modules.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        log.debug("=========== clientRegistration  ===============");
        log.debug(clientRegistration.toString());

        log.debug("=========== oAuth2User  ===============");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        log.debug(oAuth2User.toString());

        Map<String, Object> attributes = oAuth2User.getAttributes();
        ClientRegistration.ProviderDetails providerDetails = clientRegistration.getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
        String registrationId = clientRegistration.getRegistrationId();
        ProviderType provider = getProvider(registrationId);

//        유저정보로 contirbute 생성
        OAuthAttribute oAuthAttribute = OAuthAttribute.of(provider, userNameAttributeName, attributes);

        Member member = memberService.getMember(oAuthAttribute, provider);

        Set<Role> roles = member.getRoles();
        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
        OAuthUserDto userDto = new OAuthUserDto(authorities, attributes, userNameAttributeName);
        log.debug("=========== userDto  ===============");
        log.debug(userDto.toString());
        return userDto;
    }

    public ProviderType getProvider(String registrationId) {
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
