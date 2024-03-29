package com.demo.modules.repository;

import com.demo.modules.entity.Member;
import com.demo.modules.enums.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    public Optional<Member> findBySocialIdAndProviderType(String socialId, ProviderType providerType);
}
