package com.demo.modules.dto;

import com.demo.modules.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

public class MemberUser extends User {
    private static final long serialVersionUID = 6859577402581345212L;

    private final MemberDto memberDto;

    public MemberUser(Member member, String password, Collection<? extends GrantedAuthority> authorities) {
//        super(member.getId(), member.getPassword() , authorities);
        super(member.getId(), member.getPassword(), authorities != null ? authorities : Collections.emptyList());
        this.memberDto = MemberDto.builder()
                .id(member.getId())
                .password(null)
                .roles(member.getRoles())
                .email(member.getEmail())
                .providerType(member.getProviderType())
                .socialId(member.getSocialId())
                .imageUrl(member.getImageUrl())
                .build();
    }

    public MemberDto getMemberDto() {
        return memberDto;
    }
}
