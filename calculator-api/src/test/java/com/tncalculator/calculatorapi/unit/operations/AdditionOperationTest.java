package com.tncalculator.calculatorapi.unit.operations;

import com.tncalculator.calculatorapi.exceptions.InvalidOperationArgumentsException;
import com.tncalculator.calculatorapi.operations.AdditionOperation;
import com.tncalculator.calculatorapi.operations.CalculatorOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

import static com.tncalculator.calculatorapi.constants.OperationConstants.FIRST_NUMBER;
import static com.tncalculator.calculatorapi.constants.OperationConstants.SECOND_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
public class AdditionOperationTest {

    private CalculatorOperation<Double> additionOperation;

    @BeforeEach
    public void setUp() {
        additionOperation = new AdditionOperation();
    }

    @Test
    public void testRequiredParameters() {
        Set<String> requiredParameters = additionOperation.getRequiredParameters();
        assertEquals(requiredParameters, Set.of(FIRST_NUMBER, SECOND_NUMBER));
    }

    @Test
    public void testCalculateWithNullParameter() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> additionOperation.calculate(null));
    }

    @Test
    public void testCalculateWithoutParameters() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> additionOperation.calculate(Map.of()));
    }

    @Test
    public void testCalculateWithWrongParameters() {
        assertThrows(InvalidOperationArgumentsException.class,
                () -> additionOperation.calculate(Map.of("ANY_PARAM", 1.3)));
    }

    @Test
    public void testCalculation() throws Exception {
        double firstNumber = 1.3;
        double secondNumber = 2.3;
        double result = additionOperation.calculate(Map.of(FIRST_NUMBER, firstNumber, SECOND_NUMBER, secondNumber));
        assertEquals(firstNumber + secondNumber, result);
    }
}
