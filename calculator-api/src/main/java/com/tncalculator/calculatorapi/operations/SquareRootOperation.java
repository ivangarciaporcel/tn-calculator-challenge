package com.tncalculator.calculatorapi.operations;

import java.util.Map;
import java.util.Set;

import static com.tncalculator.calculatorapi.constants.OperationConstants.SINGLE_PARAMETER;

public class SquareRootOperation extends CalculatorOperation<Double>{

    private final Set<String> requiredParameters = Set.of(SINGLE_PARAMETER);

    @Override
    protected Set<String> getRequiredParameters() {
        return this.requiredParameters;
    }

    @Override
    protected Double doCalculation(Map<String, Double> parameters) {
        double singleParameter = parameters.get(SINGLE_PARAMETER);
        return Math.sqrt(singleParameter);
    }
}