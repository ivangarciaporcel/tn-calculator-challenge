package com.tncalculator.calculatorapi.security.providers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

public class JwtAuthentication implements Authentication {
    private UserDetails userDetails;
    private boolean authenticated = true;

    public JwtAuthentication(UserDetails userDetails) {
        checkArgument(userDetails != null);
        this.userDetails = userDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetails.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        authenticated = b;
    }

    @Override
    public String getName() {
        return userDetails.getUsername();
    }
}

