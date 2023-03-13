package com.tncalculator.calculatorapi.unit.operations;

import com.tncalculator.calculatorapi.exceptions.InvalidOperationArgumentsException;
import com.tncalculator.calculatorapi.operations.CalculatorOperation;
import com.tncalculator.calculatorapi.operations.SquareRootOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

import static com.tncalculator.calculatorapi.constants.OperationConstants.SINGLE_PARAMETER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
public class SquareRootOperationTest {

    private CalculatorOperation<Double> squareRootOperation;

    @BeforeEach
    public void setUp() {
        squareRootOperation = new SquareRootOperation();
    }

    @Test
    public void testRequiredParameters() {
        Set<String> requiredParameters = squareRootOperation.getRequiredParameters();
        assertEquals(requiredParameters, Set.of(SINGLE_PARAMETER));
    }

    @Test
    public void testCalculateWithNullParameter() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> squareRootOperation.calculate(null));
    }

    @Test
    public void testCalculateWithoutParameters() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> squareRootOperation.calculate(Map.of()));
    }

    @Test
    public void testCalculateWithWrongParameters() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> squareRootOperation.calculate(Map.of("ANY_PARAM", 1.3)));
    }

    @Test
    public void testCalculation() throws Exception {
        double singleParameter = 1.3;
        double result = squareRootOperation.calculate(Map.of(SINGLE_PARAMETER, singleParameter));
        assertEquals(Math.sqrt(singleParameter), result);
    }
}
