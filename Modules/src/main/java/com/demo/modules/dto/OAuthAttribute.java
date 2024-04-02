package com.demo.modules.dto;

import com.demo.modules.entity.Member;
import com.demo.modules.enums.MemberStatusEnum;
import com.demo.modules.enums.ProviderType;
import com.demo.modules.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private PasswordEncoder passwordEncoder;

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
        return Member.builder()
                .email(OAuth2UserInfo.getEmail())
                .name(OAuth2UserInfo.getNickname())
                .imageUrl(OAuth2UserInfo.getImageUrl())
                .roles(EnumSet.of(Role.ROLE_GUEST))
                .socialId(OAuth2UserInfo.getId())
                .providerType(providerType)
                .status(MemberStatusEnum.ACTIVE)
                .build();
    }

}
