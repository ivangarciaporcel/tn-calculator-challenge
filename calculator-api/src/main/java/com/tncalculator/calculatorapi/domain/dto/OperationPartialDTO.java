package com.tncalculator.calculatorapi.domain.dto;

import com.tncalculator.calculatorapi.domain.model.OperationStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPartialDTO {

    private Optional<@DecimalMin(value = "0.0", inclusive = false) Double> cost;

    private Optional<@NotNull(message = "{operation.status.not.null}") OperationStatus> status;
}
