package com.demo.api.service;

import com.demo.modules.dto.OAuth2UserInfo;
import com.demo.modules.dto.OAuthAttribute;
import com.demo.modules.entity.Member;
import com.demo.modules.enums.ProviderType;
import com.demo.modules.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    /* 소셜 회원 처리 */
    @Transactional
    public Member getSocialMember(OAuthAttribute oAuthAttribute, ProviderType providerType) {
        Optional<Member> socialMember = memberRepository.findBySocialIdAndProviderType(oAuthAttribute.getOAuth2UserInfo().getId(), providerType);
        if (socialMember.isPresent()) {
            Member existingMember = socialMember.get();
            return updateSocialMemberInfo(existingMember, oAuthAttribute.getOAuth2UserInfo());
        } else {
            return saveSocialMember(oAuthAttribute, providerType);
        }
    }

    /*  소셜 로그인 대상자의 프로필 이미지 정책으로 인해 로그인시 이미지 현재 버전으로 변경  */
    @Transactional
    public Member updateSocialMemberInfo(Member member, OAuth2UserInfo oAuth2UserInfo) {
        String nowImageUrl = oAuth2UserInfo.getImageUrl();
        if (nowImageUrl != null && !nowImageUrl.equals(member.getImageUrl())) {
            member.setImageUrl(nowImageUrl);
            return memberRepository.save(member);
        }
        return member;
    }

    /* 소셜 로그인 회원 저장 */
    @Transactional
    public Member saveSocialMember(OAuthAttribute oAuthAttribute, ProviderType providerType) {
        Member saveMember = OAuthAttribute.toEntity(oAuthAttribute.getOAuth2UserInfo(), providerType);
        String password = passwordEncoder.encode("social");
        saveMember.setPassword(password);
        Member saved = memberRepository.save(saveMember);
        return saved;
    }

}
