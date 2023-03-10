package com.tncalculator.calculatorapi.security.filters;

import com.tncalculator.calculatorapi.exceptions.RestExceptionHandler;
import com.tncalculator.calculatorapi.security.providers.JwtAuthenticationToken;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

public class JwtAuthenticationFilter extends GenericFilterBean {
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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse) {
            String header = httpRequest.getHeader(HEADER_AUTHORIZATION);
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
        }

        chain.doFilter(request, response);
    }
}

