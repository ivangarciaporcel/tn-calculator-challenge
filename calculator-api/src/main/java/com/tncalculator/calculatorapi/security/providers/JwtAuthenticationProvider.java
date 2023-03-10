package com.tncalculator.calculatorapi.security.providers;

import com.tncalculator.calculatorapi.security.jwt.JwtTokenDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import static com.google.common.base.Preconditions.checkArgument;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private JwtTokenDecoder jwtTokenDecoder;

    @Autowired
    public JwtAuthenticationProvider(JwtTokenDecoder jwtTokenDecoder) {
        checkArgument(jwtTokenDecoder != null);
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        UserDetails userDetails = jwtTokenDecoder.decode((String) token.getCredentials());
        return new JwtAuthentication(userDetails);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(JwtAuthenticationToken.class);
    }
}
