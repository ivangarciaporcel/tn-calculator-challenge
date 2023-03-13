package com.tncalculator.calculatorapi.http.clients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RandomHttpClientTest {

    private RandomHttpClient randomHttpClient;
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        randomHttpClient = new RandomHttpClient(restTemplate);
    }

    @Test
    public void testHttpClientFailsGeneratingRandomString() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(ResponseEntity.badRequest().build());
        assertThrows(IllegalArgumentException.class,
                () -> randomHttpClient.generateRandomString());
    }
    
    @Test
    public void testGenerateRandomString() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(ResponseEntity.ok().body(UUID.randomUUID().toString()));
        String generatedString = randomHttpClient.generateRandomString();
        assertFalse(StringUtils.isBlank(generatedString));
    }
}
