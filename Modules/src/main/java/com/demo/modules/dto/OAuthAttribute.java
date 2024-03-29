package com.demo.modules.dto;

import com.demo.modules.entity.Member;
import com.demo.modules.enums.MemberStatusEnum;
import com.demo.modules.enums.ProviderType;
import com.demo.modules.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthAttribute {
    private String nameAttributeKey;
    private OAuth2UserInfo OAuth2UserInfo;

    public static OAuthAttribute of(ProviderType socialType, String nameAttributeKey, Map<String, Object> attributes) {
        if (socialType == ProviderType.KAKAO) {
            return ofKakao(nameAttributeKey, attributes);
        } else if (socialType == ProviderType.GOOGLE) {
            return ofGoogle(nameAttributeKey, attributes);
        } else if (socialType == ProviderType.NAVER) {

        }
        return null;
    }

    private static OAuthAttribute ofKakao(String nameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttribute.builder()
                .nameAttributeKey(nameAttributeKey)
                .OAuth2UserInfo(new KakaoUserDto(attributes))
                .build();
    }

    private static OAuthAttribute ofGoogle(String nameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttribute.builder()
                .nameAttributeKey(nameAttributeKey)
                .OAuth2UserInfo(new GoogleUserDto(attributes))
                .build();
    }

    /* 최초 소셜 로그인 회원 가입 시 사용 - 권한 게스트 권한 고정으로 박아두기 */
    public static Member toEntity(OAuth2UserInfo OAuth2UserInfo, ProviderType providerType) {
        Member member = new Member();
        member.setName(OAuth2UserInfo.getNickname());
        Set<Role> role = EnumSet.of(Role.ROLE_GUEST);
        member.setRoles(role);
        member.setEmail(UUID.randomUUID() + "@socailEmail.com");
        member.setSocialId(OAuth2UserInfo.getId());
        member.setProviderType(providerType);
        member.setImageUrl(OAuth2UserInfo.getImageUrl());
        member.setStatus(MemberStatusEnum.ACTIVE);
        return member;
    }

}
