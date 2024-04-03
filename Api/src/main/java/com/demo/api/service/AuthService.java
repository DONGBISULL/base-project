package com.demo.api.service;

import com.demo.api.security.provider.JwtTokenProvider;
import com.demo.modules.dto.JwtTokenDto;
import com.demo.modules.dto.MemberDto;
import com.demo.modules.entity.Member;
import com.demo.modules.entity.Token;
import com.demo.modules.enums.TokenStatus;
import com.demo.modules.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenRepository tokenRepository;

    private final MemberService memberService;

    @Value("${jwt.access-token.expire-time}")
    private Long accessTokenExpireTime;

    public void reissueToken(String refreshToken) {
        /* 유효성 검사*/
        validateRefreshToken(refreshToken);
        /* */
        String memberId = jwtTokenProvider.getJwtCliamByMemberId(refreshToken);
        MemberDto member = memberService.getMember(memberId);
        long now = (new Date()).getTime();
        String accessToken = jwtTokenProvider.generateAccessToken(member, now);
        jwtTokenProvider.accessTokenWithCookie(JwtTokenDto.builder()
                        .accessToken(accessToken)
                        .accessExpirationDate(new Date(now + accessTokenExpireTime))
                        .build());

    }

    private void validateRefreshToken(String refreshToken) {
        if (TokenStatus.VALID != jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 refresh 토큰");
        }

        Optional<Token> tokenInfoOptional = tokenRepository.findByDeleteYnAndRefreshToken("N", refreshToken);
        Token tokenInfo = tokenInfoOptional.get();

        if (!tokenInfoOptional.isPresent()) {
            throw new IllegalArgumentException("유효하지 않은 토큰 정보입니다.");
        }

        String memberId = jwtTokenProvider.getJwtCliamByMemberId(refreshToken);
        Member tokenInfoMember = tokenInfo.getMember();
        String refreshTokenMemberId = tokenInfoMember.getId();

        if (!refreshTokenMemberId.equals(memberId)) {
            throw new IllegalArgumentException("로그인한 사용자의 refresh 토큰이 아닙니다.");
        }

        if (tokenInfo.getExpirationDate().before(new Date())) {
            tokenInfo.setDeleteYn("Y"); // 토큰을 무효화하거나 삭제
            tokenRepository.save(tokenInfo); // 변경 내용을 데이터베이스에 저장
            throw new IllegalArgumentException("토큰의 유효 기간이 만료되었습니다.");
        }
    }

}
