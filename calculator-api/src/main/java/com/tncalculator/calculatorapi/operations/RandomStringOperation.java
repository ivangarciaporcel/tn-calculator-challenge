package com.tncalculator.calculatorapi.operations;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RandomStringOperation extends CalculatorOperation<String>{

    private final Set<String> requiredParameters = Set.of();

    @Override
    public Set<String> getRequiredParameters() {
        return this.requiredParameters;
    }

    @Override
    protected String doCalculation(Map<String, Double> parameters) {
        return UUID.randomUUID().toString();
    }
}
