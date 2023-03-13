package com.tncalculator.calculatorapi.operations;

import com.tncalculator.calculatorapi.http.clients.RandomHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class RandomStringOperation extends CalculatorOperation<String> {

    private final RandomHttpClient randomHttpClient;

    @Autowired
    public RandomStringOperation(RandomHttpClient randomHttpClient) {
        this.randomHttpClient = randomHttpClient;
    }

    private final Set<String> requiredParameters = Set.of();

    @Override
    public Set<String> getRequiredParameters() {
        return this.requiredParameters;
    }

    @Override
    protected String doCalculation(Map<String, Double> parameters) {
        return randomHttpClient.generateRandomString();
    }
}
