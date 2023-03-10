package com.tncalculator.calculatorapi.security.providers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class BasicAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String userName;

    public BasicAuthenticationToken(String userName, String credentials) {
        super(userName, credentials);
        this.userName = userName;
    }

    @Override
    public String getName() {
        return this.userName;
    }
}

