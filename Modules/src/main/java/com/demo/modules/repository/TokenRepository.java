package com.demo.modules.repository;

import com.demo.modules.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long > {
    public Optional<Token> findByDeleteYnAndMemberId(String deleteYn, String memberId);

    public Optional<Token> findByDeleteYnAndRefreshToken(String deleteYn, String refreshToken);
}
