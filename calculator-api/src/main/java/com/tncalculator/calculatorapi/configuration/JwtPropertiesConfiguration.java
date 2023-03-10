package com.tncalculator.calculatorapi.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtPropertiesConfiguration {

    private String secret;

    @Value("${jwt.expiration.seconds:6000}")
    private int expirationSeconds;
}
