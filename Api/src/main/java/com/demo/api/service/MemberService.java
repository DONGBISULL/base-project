package com.demo.api.service;

import com.demo.modules.dto.OAuthAttribute;
import com.demo.modules.entity.Member;
import com.demo.modules.enums.ProviderType;
import com.demo.modules.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    @Transactional
    public Member getMember(OAuthAttribute oAuthAttribute, ProviderType providerType) {
        Optional<Member> socialMember = memberRepository.findBySocialIdAndProviderType(oAuthAttribute.getOAuth2UserInfo().getId(), providerType);
        if (!socialMember.isPresent()) {
            return saveSocialMember(oAuthAttribute, providerType);
        }
        return socialMember.get();
    }

    @Transactional
    public Member saveSocialMember(OAuthAttribute oAuthAttribute, ProviderType providerType) {
        Member saveMember = OAuthAttribute.toEntity(oAuthAttribute.getOAuth2UserInfo(), providerType);
        Member saved = memberRepository.save(saveMember);
        return saved;
    }
}
