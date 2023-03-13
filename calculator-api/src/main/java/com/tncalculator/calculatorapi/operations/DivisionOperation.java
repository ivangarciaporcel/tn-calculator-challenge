package com.tncalculator.calculatorapi.operations;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.MessageConstants.DENOMINATOR_NOT_ZERO;
import static com.tncalculator.calculatorapi.constants.OperationConstants.DENOMINATOR;
import static com.tncalculator.calculatorapi.constants.OperationConstants.NUMERATOR;

public class DivisionOperation extends CalculatorOperation<Double>{

    private final Set<String> requiredParameters = Set.of(NUMERATOR, DENOMINATOR);

    @Override
    public Set<String> getRequiredParameters() {
        return this.requiredParameters;
    }

    @Override
    protected Double doCalculation(Map<String, Double> parameters) {
        double numerator = parameters.get(NUMERATOR);
        double denominator = parameters.get(DENOMINATOR);
        checkArgument(denominator!=0.0, DENOMINATOR_NOT_ZERO);
        return numerator / denominator;
    }
}
