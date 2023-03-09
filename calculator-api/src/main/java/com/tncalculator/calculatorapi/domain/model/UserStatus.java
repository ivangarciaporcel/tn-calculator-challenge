package com.tncalculator.calculatorapi.domain.model;

public enum UserStatus {

    ACTIVE("active"),
    INACTIVE("inactive");

    private final String name;

    UserStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return this.getName();
    }
}
