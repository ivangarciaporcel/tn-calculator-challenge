package com.tncalculator.calculatorapi.operations;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.tncalculator.calculatorapi.constants.MessageConstants.OPERATION_NOT_IMPLEMENTED;
import static com.tncalculator.calculatorapi.constants.OperationConstants.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OperationsFactory {

    public static CalculatorOperation<?> getOperation(String operationType) {
        return switch (operationType) {
            case ADDITION -> new AdditionOperation();
            case SUBTRACTION -> new SubtractionOperation();
            case MULTIPLICATION -> new MultiplicationOperation();
            case DIVISION -> new DivisionOperation();
            case SQUARE_ROOT -> new SquareRootOperation();
            case RANDOM_STRING -> new RandomStringOperation();
            default -> throw new IllegalArgumentException(OPERATION_NOT_IMPLEMENTED);
        };
    }
}
