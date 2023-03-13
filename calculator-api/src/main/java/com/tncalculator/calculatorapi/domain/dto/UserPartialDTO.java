package com.tncalculator.calculatorapi.domain.dto;

import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.validation.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPartialDTO {

    private Optional<@NotBlank(message = "{password.not.blank}") String> password;

    private Optional<@NotNull(message = "{user.status.not.null}") UserStatus> status;

    private Optional<@UserRole Set<String>> roles;
}
