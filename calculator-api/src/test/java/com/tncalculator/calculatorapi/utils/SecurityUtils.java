package com.tncalculator.calculatorapi.utils;

import com.tncalculator.calculatorapi.configuration.InMemoryUserDetailsManager;
import com.tncalculator.calculatorapi.domain.model.Authority;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.security.Roles;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static com.tncalculator.calculatorapi.utils.Builders.authority;
import static com.tncalculator.calculatorapi.utils.Builders.user;

@TestConfiguration
public class SecurityUtils {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User adminUser = user("admin");
        Authority adminAuthority = authority(Roles.ADMIN, adminUser);
        adminUser.setAuthorities(List.of(adminAuthority));

        User commonUser = user("user1");
        Authority userAuthority = authority(Roles.USER, commonUser);
        commonUser.setAuthorities(List.of(userAuthority));
        return new InMemoryUserDetailsManager(List.of(adminUser, commonUser));
    }
}
