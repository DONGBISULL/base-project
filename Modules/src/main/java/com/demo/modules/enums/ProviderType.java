package com.demo.modules.enums;

public enum ProviderType {
    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글"),
    NOMAL("일반회원");
    private final String name;

    ProviderType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
