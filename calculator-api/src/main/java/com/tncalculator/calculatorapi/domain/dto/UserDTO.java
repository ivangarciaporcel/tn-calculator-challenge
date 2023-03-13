package com.tncalculator.calculatorapi.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.validation.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Email(message = "{email.not.valid}")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "{password.not.blank}")
    private String password;

    @NotNull(message = "{user.status.not.null}")
    private UserStatus status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double balance;

    @UserRole
    private Set<String> roles;

}
