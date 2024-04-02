package com.demo.modules.dto;

import com.demo.modules.enums.MemberStatusEnum;
import com.demo.modules.enums.ProviderType;
import com.demo.modules.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Builder
public class MemebrDto {
    private String id;
    private String password;
    private String name;
    private String phoneNumber;
    private String department;
    private String email;
    private MemberStatusEnum status;
    private String socialId;
    private ProviderType providerType;
    private Set<Role> roles;
    private String imageUrl;
}
