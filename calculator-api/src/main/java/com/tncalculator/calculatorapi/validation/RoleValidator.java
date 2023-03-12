package com.tncalculator.calculatorapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_CALCULATOR;

public class RoleValidator implements ConstraintValidator<UserRole, Set<String>> {

    private final Set<String> VALID_ROLES = Set.of(USER_ADMIN, USER_CALCULATOR);

    @Override
    public boolean isValid(Set<String> roles, ConstraintValidatorContext context) {
        return roles != null
                && !roles.isEmpty()
                && VALID_ROLES.containsAll(roles);
    }
}
