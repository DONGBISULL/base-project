package com.demo.modules.repository;

import com.demo.modules.entity.Member;
import com.demo.modules.enums.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    /* 소셜 로그인한 회원 소셜 로그인 아이디와 로그인 매체로 회원 확인  */
    public Optional<Member> findBySocialIdAndProviderType(String socialId, ProviderType providerType);
}
