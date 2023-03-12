package com.tncalculator.calculatorapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoleValidator.class)
@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserRole {
    String message() default "There is one or more invalid roles";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
