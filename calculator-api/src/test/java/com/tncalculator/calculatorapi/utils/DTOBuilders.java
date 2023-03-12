package com.tncalculator.calculatorapi.utils;

import com.tncalculator.calculatorapi.domain.dto.AuthRequestDTO;
import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.model.UserStatus;

import java.util.Set;

public class DTOBuilders {

    private DTOBuilders() {
    }

    public static UserDTO userDTO(String email, String password, UserStatus status, double balance, Set<String> roles) {
        return UserDTO.builder()
                .email(email)
                .password(password)
                .status(status)
                .balance(balance)
                .roles(roles)
                .build();
    }

}
