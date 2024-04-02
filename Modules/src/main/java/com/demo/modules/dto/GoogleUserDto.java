package com.demo.modules.dto;

import java.util.Map;

public class GoogleUserDto extends OAuth2UserInfo {
    public GoogleUserDto(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public String getNickname() {
        String nickname = (String) attributes.get("name");
        if(nickname == null){
            return null;
        }
        return nickname;
    }

    @Override
    public String getImageUrl() {
        String imageUrl = (String) attributes.get("picture");
        if(imageUrl == null){
            return null;
        }
        return imageUrl;
    }

    @Override
    public String getEmail() {
        String email = (String) attributes.get("email");
        boolean isValid = (boolean) attributes.get("email_verified");
        if (email == null) {
            return null;
        }

        if (!isValid) {
            return null;
        }
        return email;
    }
}
