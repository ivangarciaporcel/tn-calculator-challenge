package com.tncalculator.calculatorapi.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tncalculator.calculatorapi.domain.model.OperationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordDTO {

    private UUID id;

    private OperationDTO operation;

    private UserDTO user;

    private double amount;

    @JsonProperty("user_balance")
    private double userBalance;

    @JsonProperty("operation_response")
    private OperationResponse operationResponse;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

}
