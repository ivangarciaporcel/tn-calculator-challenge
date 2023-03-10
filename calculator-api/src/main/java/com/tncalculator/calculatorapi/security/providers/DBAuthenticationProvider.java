package com.tncalculator.calculatorapi.security.providers;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class DBAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        return super.createSuccessAuthentication(principal, authentication, user);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(BasicAuthenticationToken.class);
    }

}

