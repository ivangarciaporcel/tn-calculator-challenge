package com.tncalculator.calculatorapi.security.filters;

import com.tncalculator.calculatorapi.exceptions.RestExceptionHandler;
import com.tncalculator.calculatorapi.security.providers.JwtAuthenticationToken;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_TYPE = "Bearer ";

    private final AuthenticationManager authenticationManager;

    private final RestExceptionHandler restExceptionHandler;

    @Autowired
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, RestExceptionHandler restExceptionHandler) {
        checkArgument(authenticationManager != null);
        this.authenticationManager = authenticationManager;
        this.restExceptionHandler = restExceptionHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String header = request.getHeader(HEADER_AUTHORIZATION);
            if (header != null && header.startsWith(AUTHORIZATION_TYPE)) {
                String token = header.substring(AUTHORIZATION_TYPE.length()).trim();
                JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);

                try {
                    Authentication authentication = authenticationManager.authenticate(authenticationToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (ExpiredJwtException e) {
                    restExceptionHandler.handleCredentialsExpiredException(new CredentialsExpiredException(e.getMessage(), e));
                } catch (Exception e) {
                    restExceptionHandler.handleBadCredentialsException(new BadCredentialsException(e.getMessage(), e));
                }
            }
        filterChain.doFilter(request, response);
    }

}

