package com.tncalculator.calculatorapi.unit.operations;

import com.tncalculator.calculatorapi.exceptions.InvalidOperationArgumentsException;
import com.tncalculator.calculatorapi.operations.CalculatorOperation;
import com.tncalculator.calculatorapi.operations.SubtractionOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

import static com.tncalculator.calculatorapi.constants.OperationConstants.MINUEND;
import static com.tncalculator.calculatorapi.constants.OperationConstants.SUBTRAHEND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
public class SubtractionOperationTest {

    private CalculatorOperation<Double> subtractionOperation;

    @BeforeEach
    public void setUp() {
        subtractionOperation = new SubtractionOperation();
    }

    @Test
    public void testRequiredParameters() {
        Set<String> requiredParameters = subtractionOperation.getRequiredParameters();
        assertEquals(requiredParameters, Set.of(MINUEND, SUBTRAHEND));
    }

    @Test
    public void testCalculateWithNullParameter() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> subtractionOperation.calculate(null));
    }

    @Test
    public void testCalculateWithoutParameters() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> subtractionOperation.calculate(Map.of()));
    }

    @Test
    public void testCalculateWithWrongParameters() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> subtractionOperation.calculate(Map.of("ANY_PARAM", 1.3)));
    }

    @Test
    public void testCalculation() throws Exception {
        double minuend = 1.3;
        double subtrahend = 2.3;
        double result = subtractionOperation.calculate(Map.of(MINUEND, minuend, SUBTRAHEND, subtrahend));
        assertEquals(minuend - subtrahend, result);
    }
}
