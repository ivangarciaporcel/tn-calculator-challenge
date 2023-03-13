package com.tncalculator.calculatorapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityMock {

    protected Authentication authentication;

    protected SecurityContext securityContext;

    protected UserDetails userDetails;

    @BeforeEach
    protected void setUpSecurity() {
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        userDetails = new User("admin", "", true, true, true, true, Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
