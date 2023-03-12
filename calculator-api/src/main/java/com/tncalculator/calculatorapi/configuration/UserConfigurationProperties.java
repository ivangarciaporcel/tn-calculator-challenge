package com.tncalculator.calculatorapi.configuration;

import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Configuration
@ConfigurationProperties(prefix = "user")
@Getter
@Setter
public class UserConfigurationProperties {

    @DecimalMin("0.0")
    @Value("${user.balance.initial:100.0}")
    private Double initialBalance;
}
