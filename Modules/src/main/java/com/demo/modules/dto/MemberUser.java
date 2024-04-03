package com.demo.modules.dto;

import com.demo.modules.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class MemberUser extends User {
    private static final long serialVersionUID = 6859577402581345212L;

    private final MemberDto memberDto;

    public MemberUser(Member member, String password, Collection<? extends GrantedAuthority> authorities) {
        super(member.getId(), member.getPassword() , authorities);
        this.memberDto = MemberDto.builder()
                .id(member.getId())
                .password(null)
                .email(member.getEmail())
                .password(password)
                .providerType(member.getProviderType())
                .socialId(member.getSocialId())
                .build();
    }
    public MemberDto getMemberDto() {
        return memberDto;
    }
}
