package com.demo.modules.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
public class JwtTokenDto {

    private String id;  // 토큰 Id

    private String memberId; // 회원 Id

    private String refreshToken; // 리프레시 토큰

    private LocalDateTime refreshExpirationDate; // 리프레시 토큰 만료시간

    private String accessToken; // 액세스 토큰

    private LocalDateTime accessExpirationDate;; // 액세스 토큰 만료시간
}
