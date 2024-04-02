package com.demo.modules.dto;

import java.util.Map;

public class KakaoUserDto extends OAuth2UserInfo {

    public KakaoUserDto(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getNickname() {
        Map<String, Object> kakaoAcount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAcount == null) {
            return null;
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAcount.get("profile");
        if (profile == null) {
            return null;
        }

        return (String) profile.get("nickname");
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> kakaoAcount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAcount == null) {
            return null;
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAcount.get("profile");
        if (profile == null) {
            return null;
        }

        return (String) profile.get("profile_image_url");
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAcount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAcount == null) {
            return null;
        }
        boolean hasEmail = (boolean) kakaoAcount.get("has_email");

        if (!hasEmail) {
            return null;
        }

        boolean isEmailVerified = (boolean) kakaoAcount.get("is_email_verified");
        if (!isEmailVerified) {
            return null;
        }

        boolean isEmailValid = (boolean) kakaoAcount.get("is_email_valid");
        if (!isEmailValid) {
            return null;
        }

        return (String) kakaoAcount.get("email");
    }
}
