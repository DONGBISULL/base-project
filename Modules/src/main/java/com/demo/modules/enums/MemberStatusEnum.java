package com.demo.modules.enums;

public enum MemberStatusEnum {
    ACTIVE("가입"),
    INACTIVE("휴면"),
    BLOCKED("차단"),
    WAITING("대기");

    private final String name;

    MemberStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
