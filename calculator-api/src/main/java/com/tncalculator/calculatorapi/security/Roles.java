package com.tncalculator.calculatorapi.security;

public enum Roles {

    ADMIN("admin"),
    USER_MANAGER("user_manager"),
    USER("user");

    private final String name;

    Roles(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
