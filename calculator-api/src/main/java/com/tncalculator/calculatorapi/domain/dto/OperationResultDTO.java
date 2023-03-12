package com.tncalculator.calculatorapi.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tncalculator.calculatorapi.domain.model.OperationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationResultDTO {

    private Object result;

    @JsonProperty("operation_response")
    private OperationResponse operationResponse;
}


