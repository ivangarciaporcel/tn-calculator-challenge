package com.tncalculator.calculatorapi.integration.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.OperationDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationPartialDTO;
import com.tncalculator.calculatorapi.domain.model.Operation;
import com.tncalculator.calculatorapi.domain.model.OperationStatus;
import com.tncalculator.calculatorapi.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static com.tncalculator.calculatorapi.configuration.SecurityConfiguration.ADMIN_USER;
import static com.tncalculator.calculatorapi.utils.DTOBuilders.operationDTO;
import static com.tncalculator.calculatorapi.utils.DTOBuilders.operationPartialDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatchOperationIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/operations/%s";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testPatchOperationWithInvalidUUID() throws URISyntaxException {
        URI uri = new URI(String.format(url, "anyId"));
        OperationPartialDTO operationPartialDTO = operationPartialDTO(1.4, OperationStatus.IN_VERIFICATION);

        HttpEntity<OperationPartialDTO> request = new HttpEntity<>(operationPartialDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PATCH, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void testPatchOperationNotFound() throws URISyntaxException {
        URI uri = new URI(String.format(url, UUID.randomUUID()));
        OperationPartialDTO operationPartialDTO = operationPartialDTO(1.4, OperationStatus.IN_VERIFICATION);

        HttpEntity<OperationPartialDTO> request = new HttpEntity<>(operationPartialDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PATCH, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    public void testPatchOperation() throws URISyntaxException, JsonProcessingException {
        Operation operation = createOperation("EXISTENT_OPERATION", OperationStatus.APPROVED);
        URI uri = new URI(String.format(url, operation.getId()));
        OperationPartialDTO operationPartialDTO = operationPartialDTO(operation.getCost() + 1.4, OperationStatus.IN_VERIFICATION);

        HttpEntity<OperationPartialDTO> request = new HttpEntity<>(operationPartialDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PATCH, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        OperationDTO response = getResponse(result.getBody(), OperationDTO.class);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(operation.getId(), response.getId()),
                () -> assertEquals(operation.getType(), response.getType()),
                () -> assertNotEquals(operation.getCost(), response.getCost()),
                () -> assertNotEquals(operation.getStatus(), response.getStatus())
        );
    }
}
