package com.tncalculator.calculatorapi.security.jwt;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.tncalculator.calculatorapi.configuration.JwtPropertiesConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JwtTokenDecoder {

    private static final Splitter AUTH_SPLIT = Splitter.on(',').trimResults().omitEmptyStrings();
    private final JwtPropertiesConfiguration jwtPropertiesConfiguration;

    public JwtTokenDecoder(JwtPropertiesConfiguration jwtPropertiesConfiguration) {
        this.jwtPropertiesConfiguration = jwtPropertiesConfiguration;
    }
    public UserDetails decode(String token) {
        if (Strings.isNullOrEmpty(token)) {
            return null;
        }

        Claims claims = Jwts.parser()
                .setSigningKey(jwtPropertiesConfiguration.getSecret())
                .parseClaimsJws(token)
                .getBody();

        Collection<GrantedAuthority> authorities =
                claims.containsKey(TokenClaims.AUTHORITIES) ?
                        StreamSupport.stream(AUTH_SPLIT.split((String) claims.get(TokenClaims.AUTHORITIES)).spliterator(), false)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()) :
                        Collections.emptyList();

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime expiry = ZonedDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneOffset.UTC);

        boolean credentialsExpired = ChronoUnit.MILLIS.between(expiry, now) > 0;

        return new User(claims.getSubject(), "", true, true, !credentialsExpired, true, authorities);
    }
}
