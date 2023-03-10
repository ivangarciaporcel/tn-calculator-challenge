package com.tncalculator.calculatorapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role implements GrantedAuthority {

    public static final String USER_ADMIN = "USER_ADMIN";
    public static final String USER_CALCULATOR = "USER_CALCULATOR";
    private String authority;

}
