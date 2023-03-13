package com.tncalculator.calculatorapi.unit.operations;

import com.tncalculator.calculatorapi.exceptions.InvalidOperationArgumentsException;
import com.tncalculator.calculatorapi.operations.CalculatorOperation;
import com.tncalculator.calculatorapi.operations.DivisionOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

import static com.tncalculator.calculatorapi.constants.OperationConstants.DENOMINATOR;
import static com.tncalculator.calculatorapi.constants.OperationConstants.NUMERATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
public class DivisionOperationTest {

    private CalculatorOperation<Double> divisionOperation;

    @BeforeEach
    public void setUp() {
        divisionOperation = new DivisionOperation();
    }

    @Test
    public void testRequiredParameters() {
        Set<String> requiredParameters = divisionOperation.getRequiredParameters();
        assertEquals(requiredParameters, Set.of(NUMERATOR, DENOMINATOR));
    }

    @Test
    public void testCalculateWithNullParameter() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> divisionOperation.calculate(null));
    }

    @Test
    public void testCalculateWithoutParameters() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> divisionOperation.calculate(Map.of()));
    }

    @Test
    public void testCalculateWithWrongParameters() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> divisionOperation.calculate(Map.of("ANY_PARAM", 1.3)));
    }

    @Test
    public void testWithDenominatorZero() {
        double numerator = 1.3;
        double denominator = 0.0;
        assertThrows(IllegalArgumentException.class,
                () -> divisionOperation.calculate(Map.of(NUMERATOR, numerator, DENOMINATOR, denominator)));
    }

    @Test
    public void testCalculation() throws Exception {
        double numerator = 1.3;
        double denominator = 0.3;
        double result = divisionOperation.calculate(Map.of(NUMERATOR, numerator, DENOMINATOR, denominator));
        assertEquals(numerator / denominator, result);
    }
}

