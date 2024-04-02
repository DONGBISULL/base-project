package com.demo.modules.dto;

import com.demo.modules.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MemberUser extends User {
    private static final long serialVersionUID = 6859577402581345212L;

    private final MemebrDto memebrDto;

    public MemberUser(Member member, String password, Collection<? extends GrantedAuthority> authorities) {
        super(member.getId(), member.getPassword() , authorities);
        this.memebrDto = MemebrDto.builder()
                .id(member.getId())
                .password(null)
                .email(member.getEmail())
                .password(password)
                .providerType(member.getProviderType())
                .socialId(member.getSocialId())
                .build();
    }


    public MemebrDto getMemebrDto() {
        return memebrDto;
    }
}
