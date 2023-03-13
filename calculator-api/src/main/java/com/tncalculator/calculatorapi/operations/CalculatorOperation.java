package com.tncalculator.calculatorapi.operations;

import com.tncalculator.calculatorapi.exceptions.InvalidOperationArgumentsException;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.tncalculator.calculatorapi.constants.MessageConstants.INVALID_OPERATION_PARAMETERS;
import static com.tncalculator.calculatorapi.constants.MessageConstants.OPERATION_SHOULD_NOT_HAVE_PARAMETERS;

public abstract class CalculatorOperation<R> {

    abstract public Set<String> getRequiredParameters();

    abstract protected R doCalculation(Map<String, Double> parameters);

    public R calculate(Map<String, Double> parameters) throws Exception {
        Set<String> requiredParameters = getRequiredParameters();
        if (!requiredParameters.equals(parameters != null ? parameters.keySet() : Collections.emptySet())) {
            if (requiredParameters.isEmpty()) {
                throw new IllegalArgumentException(OPERATION_SHOULD_NOT_HAVE_PARAMETERS);
            }
            throw new InvalidOperationArgumentsException(INVALID_OPERATION_PARAMETERS,
                    new Object[]{String.join(",", requiredParameters)});
        }
        return doCalculation(parameters);
    }

}
