package com.demo.modules.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomUserDetail implements OAuth2User, UserDetails {

    private MemberUser user;
    private Map<String, Object> attributes;

    public CustomUserDetail(MemberUser user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public CustomUserDetail(MemberUser user) {
        this.user = user;
    }

    public CustomUserDetail(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public MemberDto getMember(){
        return user.getMemberDto();
    }
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getMemberDto().getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public Map<String, Object> getAttributes() {
        if (attributes != null) {
            return attributes;
        } else {
            return Collections.emptyMap(); // 기본값으로 빈 맵을 반환하도록 수정
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getName() {
        return user.getMemberDto().getId();
    }
}
