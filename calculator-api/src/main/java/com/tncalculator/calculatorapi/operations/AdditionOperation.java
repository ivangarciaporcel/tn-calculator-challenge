package com.tncalculator.calculatorapi.operations;

import java.util.Map;
import java.util.Set;

import static com.tncalculator.calculatorapi.constants.OperationConstants.FIRST_NUMBER;
import static com.tncalculator.calculatorapi.constants.OperationConstants.SECOND_NUMBER;

public class AdditionOperation extends CalculatorOperation<Double>{

    private final Set<String> requiredParameters = Set.of(FIRST_NUMBER, SECOND_NUMBER);

    @Override
    protected Set<String> getRequiredParameters() {
        return this.requiredParameters;
    }

    @Override
    protected Double doCalculation(Map<String, Double> parameters) {
        double firstNumber = parameters.get(FIRST_NUMBER);
        double secondNumber = parameters.get(SECOND_NUMBER);
        return firstNumber + secondNumber;
    }
}
