package com.tncalculator.calculatorapi.utils;

import com.tncalculator.calculatorapi.domain.dto.*;
import com.tncalculator.calculatorapi.domain.model.OperationStatus;
import com.tncalculator.calculatorapi.domain.model.UserStatus;

import java.util.Map;
import java.util.Optional;
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

    public static UserPartialDTO userPartialDTO(String password, UserStatus status, Set<String> roles) {
        return UserPartialDTO.builder()
                .password(Optional.of(password))
                .status(Optional.of(status))
                .roles(Optional.of(roles))
                .build();
    }

    public static OperationDTO operationDTO(String type, double cost, OperationStatus status) {
        return OperationDTO.builder()
                .type(type)
                .cost(cost)
                .status(status)
                .build();
    }

    public static OperationPartialDTO operationPartialDTO(double cost, OperationStatus operationStatus) {
        return OperationPartialDTO.builder()
                .cost(Optional.of(cost))
                .status(Optional.of(operationStatus))
                .build();
    }

    public static CalculatorOperationsDTO calculatorOperationsDTO(Map<String, Double> parameters) {
        return CalculatorOperationsDTO.builder()
                .parameters(parameters)
                .build();
    }
}
