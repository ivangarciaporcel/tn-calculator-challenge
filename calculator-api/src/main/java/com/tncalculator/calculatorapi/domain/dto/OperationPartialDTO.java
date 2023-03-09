package com.tncalculator.calculatorapi.domain.dto;

import com.tncalculator.calculatorapi.domain.model.OperationStatus;
import jakarta.validation.constraints.NotBlank;
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

    private Optional<@NotBlank(message = "{operation.type.not.blank}") String> type;

    private Optional<Double> cost;

    private Optional<@NotBlank OperationStatus> status;
}
