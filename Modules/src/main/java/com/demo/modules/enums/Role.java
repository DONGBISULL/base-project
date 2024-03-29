package com.demo.modules.enums;

public enum Role {
    ROLE_MEMEBR("회원"),
    ROLE_ADMIN("관리자"),
    ROLE_GUEST("게스트");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
