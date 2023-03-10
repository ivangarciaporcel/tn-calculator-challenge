package com.tncalculator.calculatorapi.security.jwt;

import com.tncalculator.calculatorapi.configuration.JwtPropertiesConfiguration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class JwtTokenEncoder {
    private final JwtPropertiesConfiguration jwtPropertiesConfiguration;

    public JwtTokenEncoder(JwtPropertiesConfiguration jwtPropertiesConfiguration) {
        this.jwtPropertiesConfiguration = jwtPropertiesConfiguration;
    }

    public String encode(UserDetails userDetails) {
        checkArgument(userDetails != null);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Map<String, Object> claims = new HashMap<>();

        // Add default claims
        claims.put(TokenClaims.USERNAME, userDetails.getUsername());
        claims.put(TokenClaims.AUDIENCE, TokenAudience.WEB);
        claims.put(TokenClaims.CREATED, Date.from(now.toInstant()));

        // Add granted authorities
        claims.put(TokenClaims.AUTHORITIES,
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")));

        // Encode
        ZonedDateTime expiry = now.plus(jwtPropertiesConfiguration.getExpirationSeconds(), ChronoUnit.SECONDS);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(expiry.toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtPropertiesConfiguration.getSecret())
                .compact();
    }
}
