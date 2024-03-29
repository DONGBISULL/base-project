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
        Map<String, Object> kakaoAcount = (Map<String, Object>) attributes.get("kakao_accout");

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
        Map<String, Object> kakaoAcount = (Map<String, Object>) attributes.get("kakao_accout");

        if (kakaoAcount == null) {
            return null;
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAcount.get("profile");
        if (profile == null) {
            return null;
        }

        return (String) profile.get("thumbnail_image_url");
    }
}
