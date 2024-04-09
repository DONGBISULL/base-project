package com.demo.api.service;

import com.demo.api.security.provider.JwtTokenProvider;
import com.demo.modules.dto.JwtTokenDto;
import com.demo.modules.dto.MemberDto;
import com.demo.modules.entity.Member;
import com.demo.modules.entity.Token;
import com.demo.modules.error.ErrorCode;
import com.demo.modules.enums.TokenStatus;
import com.demo.modules.error.CustomException;
import com.demo.modules.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenRepository tokenRepository;

    private final MemberService memberService;

    @Value("${jwt.access-token.expire-time}")
    private Long accessTokenExpireTime;

    /**
     * 리프레시 토큰 확인 후 accessToken 재발행
     * */
    public JwtTokenDto reissueToken(String refreshToken) {
        /* 유효성 검사*/
        validateRefreshToken(refreshToken);
        String memberId = jwtTokenProvider.getJwtCliamByMemberId(refreshToken);
        JwtTokenDto accessTokenInfo = reissueAccessToken(memberId);
        return accessTokenInfo;
    }

    /**
     * 추후 회원 수정등으로 액세스 토큰 재발행 시 사용 예정
     */
    private JwtTokenDto reissueAccessToken(String memberId) {
        MemberDto member = memberService.getMember(memberId);
//        long now = (new Date()).getTime();
        LocalDateTime now = LocalDateTime.now();
        String accessToken = jwtTokenProvider.generateAccessToken(member, now);

        LocalDateTime accessExpirationDate = now.plus(Duration.ofMillis(accessTokenExpireTime));
        JwtTokenDto accessTokenInfo = JwtTokenDto.builder()
                .accessToken(accessToken)
                .accessExpirationDate(accessExpirationDate)
                .build();
        jwtTokenProvider.accessTokenWithCookie(accessTokenInfo);

        /* 토큰 재발행 후 Authentication 세팅 */
        Authentication authentication = jwtTokenProvider.getAuthentication(member);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return accessTokenInfo;
    }

    private void validateRefreshToken(String refreshToken) {
        if (TokenStatus.VALID != jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 refresh 토큰 정보입니다");
        }

        Optional<Token> tokenInfoOptional = tokenRepository.findByDeleteYnAndRefreshToken("N", refreshToken);
        Token tokenInfo = tokenInfoOptional.get();

        if (!tokenInfoOptional.isPresent()) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String memberId = jwtTokenProvider.getJwtCliamByMemberId(refreshToken);
        Member tokenInfoMember = tokenInfo.getMember();
        String refreshTokenMemberId = tokenInfoMember.getId();

        if (!refreshTokenMemberId.equals(memberId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST);
//            throw new IllegalArgumentException("로그인한 사용자의 refresh 토큰이 아닙니다.");
        }

        if (tokenInfo.getExpirationDate().isBefore(LocalDateTime.now())) {
            tokenInfo.setDeleteYn("Y"); // 토큰을 무효화하거나 삭제
            tokenRepository.save(tokenInfo); // 변경 내용을 데이터베이스에 저장
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_REFRESH_TOKEN);
//            throw new IllegalArgumentException("토큰의 유효 기간이 만료되었습니다.");
        }
    }

}
