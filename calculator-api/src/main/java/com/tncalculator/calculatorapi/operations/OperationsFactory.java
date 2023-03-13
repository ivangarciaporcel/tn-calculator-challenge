package com.tncalculator.calculatorapi.operations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.tncalculator.calculatorapi.constants.MessageConstants.OPERATION_NOT_IMPLEMENTED;
import static com.tncalculator.calculatorapi.constants.OperationConstants.*;

@Component
public class OperationsFactory {

    private final AdditionOperation additionOperation;
    private final SubtractionOperation subtractionOperation;
    private final MultiplicationOperation multiplicationOperation;
    private final DivisionOperation divisionOperation;
    private final SquareRootOperation squareRootOperation;
    private final RandomStringOperation randomStringOperation;

    @Autowired
    public OperationsFactory(AdditionOperation additionOperation, SubtractionOperation subtractionOperation,
                             MultiplicationOperation multiplicationOperation, DivisionOperation divisionOperation,
                             SquareRootOperation squareRootOperation, RandomStringOperation randomStringOperation) {
        this.additionOperation = additionOperation;
        this.subtractionOperation = subtractionOperation;
        this.multiplicationOperation = multiplicationOperation;
        this.divisionOperation = divisionOperation;
        this.squareRootOperation = squareRootOperation;
        this.randomStringOperation = randomStringOperation;
    }

    public CalculatorOperation<?> getOperation(String operationType) {
        return switch (operationType) {
            case ADDITION -> additionOperation;
            case SUBTRACTION -> subtractionOperation;
            case MULTIPLICATION -> multiplicationOperation;
            case DIVISION -> divisionOperation;
            case SQUARE_ROOT -> squareRootOperation;
            case RANDOM_STRING -> randomStringOperation;
            default -> throw new IllegalArgumentException(OPERATION_NOT_IMPLEMENTED);
        };
    }
}
