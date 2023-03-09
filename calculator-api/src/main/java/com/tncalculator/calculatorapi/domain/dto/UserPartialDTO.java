package com.tncalculator.calculatorapi.domain.dto;

import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.security.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPartialDTO {

    private Optional<@NotBlank(message = "{password.not.blank}") String> password;

    private Optional<@NotBlank(message = "{user.status.not.blank}") UserStatus> status;

    private Optional<@NotNull List<Roles>> roles;
}
