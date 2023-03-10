package com.tncalculator.calculatorapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
