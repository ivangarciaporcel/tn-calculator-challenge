package com.tncalculator.calculatorapi.unit.operations;

import com.tncalculator.calculatorapi.exceptions.InvalidOperationArgumentsException;
import com.tncalculator.calculatorapi.operations.CalculatorOperation;
import com.tncalculator.calculatorapi.operations.RandomStringOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
public class RandomStringOperationTest {

    private CalculatorOperation<String> randomStringOperation;

    @BeforeEach
    public void setUp() {
        randomStringOperation = new RandomStringOperation();
    }

    @Test
    public void testRequiredParameters() {
        Set<String> requiredParameters = randomStringOperation.getRequiredParameters();
        assertEquals(requiredParameters, Set.of());
    }

    @Test
    public void testCalculateWithWrongParameters() {
        assertThrows(IllegalArgumentException.class,
                () -> randomStringOperation.calculate(Map.of("ANY_PARAM", 1.3)));
    }

    @Test
    public void testCalculationWithNullParameter() throws Exception {
        String result = randomStringOperation.calculate(null);
        assertTrue(StringUtils.isNotBlank(result));
    }

    @Test
    public void testCalculation() throws Exception {
        String result = randomStringOperation.calculate(Map.of());
        assertTrue(StringUtils.isNotBlank(result));
    }
}

