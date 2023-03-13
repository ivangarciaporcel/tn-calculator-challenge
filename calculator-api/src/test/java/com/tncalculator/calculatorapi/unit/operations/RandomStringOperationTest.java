package com.tncalculator.calculatorapi.unit.operations;

import com.tncalculator.calculatorapi.http.clients.RandomHttpClient;
import com.tncalculator.calculatorapi.operations.CalculatorOperation;
import com.tncalculator.calculatorapi.operations.RandomStringOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RandomStringOperationTest {

    private CalculatorOperation<String> randomStringOperation;
    private RandomHttpClient randomHttpClient;

    @BeforeEach
    public void setUp() {
        randomHttpClient = mock(RandomHttpClient.class);
        randomStringOperation = new RandomStringOperation(randomHttpClient);
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
        String generatedString = UUID.randomUUID().toString();
        when(randomHttpClient.generateRandomString()).thenReturn(generatedString);
        String result = randomStringOperation.calculate(null);
        assertEquals(generatedString, result);
    }

    @Test
    public void testCalculation() throws Exception {
        String generatedString = UUID.randomUUID().toString();
        when(randomHttpClient.generateRandomString()).thenReturn(generatedString);
        String result = randomStringOperation.calculate(Map.of());
        assertEquals(generatedString, result);
    }
}

