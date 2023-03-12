package com.tncalculator.calculatorapi.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tncalculator.calculatorapi.domain.model.OperationStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank(message = "{operation.type.not.blank}")
    private String type;

    @DecimalMin("0.0")
    private Double cost;

    @NotNull
    private OperationStatus status;
}
