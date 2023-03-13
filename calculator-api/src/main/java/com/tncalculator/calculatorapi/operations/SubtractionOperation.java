package com.tncalculator.calculatorapi.operations;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.tncalculator.calculatorapi.constants.OperationConstants.MINUEND;
import static com.tncalculator.calculatorapi.constants.OperationConstants.SUBTRAHEND;

@Component
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
