package com.tncalculator.calculatorapi.operations;

import java.util.Map;
import java.util.Set;

import static com.tncalculator.calculatorapi.constants.OperationConstants.MINUEND;
import static com.tncalculator.calculatorapi.constants.OperationConstants.SUBTRAHEND;

public class SubtractionOperation extends CalculatorOperation<Double>{

    private final Set<String> requiredParameters = Set.of(MINUEND, SUBTRAHEND);

    @Override
    public Set<String> getRequiredParameters() {
        return this.requiredParameters;
    }

    @Override
    protected Double doCalculation(Map<String, Double> parameters) {
        double minuend = parameters.get(MINUEND);
        double subtrahend = parameters.get(SUBTRAHEND);
        return minuend - subtrahend;
    }
}
