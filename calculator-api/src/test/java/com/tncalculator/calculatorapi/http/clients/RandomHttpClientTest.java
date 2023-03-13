package com.tncalculator.calculatorapi.http.clients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringRunner.class)
public class RandomHttpClientTest {

    private RandomHttpClient randomHttpClient;

    @BeforeEach
    public void setUp() {
        randomHttpClient = new RandomHttpClient();
    }

    @Test
    public void testGenerateRandomString() {
        String generatedString = randomHttpClient.generateRandomString();
        assertFalse(StringUtils.isBlank(generatedString));
    }
}
