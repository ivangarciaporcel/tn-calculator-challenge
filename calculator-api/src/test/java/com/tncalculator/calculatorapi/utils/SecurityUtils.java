package com.tncalculator.calculatorapi.utils;

import com.tncalculator.calculatorapi.configuration.InMemoryUserDetailsManager;
import com.tncalculator.calculatorapi.domain.model.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_CALCULATOR;
import static com.tncalculator.calculatorapi.utils.Builders.user;

@TestConfiguration
public class SecurityUtils {

    @Bean
    @Primary
    public UserDetailsService userDetailsServiceTest() {
        User adminUser = user("admin", Set.of(USER_ADMIN));
        User commonUser = user("user1", Set.of(USER_CALCULATOR));
        return new InMemoryUserDetailsManager(List.of(adminUser, commonUser));
    }
}
