package com.tncalculator.calculatorapi.operations;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.tncalculator.calculatorapi.constants.OperationConstants.SINGLE_PARAMETER;

@Component
public class SquareRootOperation extends CalculatorOperation<Double>{

    private final Set<String> requiredParameters = Set.of(SINGLE_PARAMETER);

    @Override
    public Set<String> getRequiredParameters() {
        return this.requiredParameters;
    }

    @Override
    protected Double doCalculation(Map<String, Double> parameters) {
        double singleParameter = parameters.get(SINGLE_PARAMETER);
        return Math.sqrt(singleParameter);
    }
}