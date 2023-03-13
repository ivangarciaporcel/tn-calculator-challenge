package com.tncalculator.calculatorapi.integration.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.OperationDTO;
import com.tncalculator.calculatorapi.domain.model.Operation;
import com.tncalculator.calculatorapi.domain.model.OperationStatus;
import com.tncalculator.calculatorapi.exceptions.ApiError;
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
import static com.tncalculator.calculatorapi.constants.MessageConstants.OPERATION_TYPE_CANNOT_BE_MODIFIED;
import static com.tncalculator.calculatorapi.utils.DTOBuilders.operationDTO;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UpdateOperationIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/operations/%s";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testUpdateOperationWithInvalidUUID() throws URISyntaxException {
        URI uri = new URI(String.format(url, "anyId"));
        OperationDTO operationDTO = operationDTO("NEW_OPERATION", 1.4, OperationStatus.IN_VERIFICATION);

        HttpEntity<OperationDTO> request = new HttpEntity<>(operationDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void testUpdateOperationNotFound() throws URISyntaxException {
        URI uri = new URI(String.format(url, UUID.randomUUID()));
        OperationDTO operationDTO = operationDTO("NEW_OPERATION", 1.4, OperationStatus.IN_VERIFICATION);

        HttpEntity<OperationDTO> request = new HttpEntity<>(operationDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    public void testUpdateOperationModifyingType() throws URISyntaxException {
        Operation existentOperation = createOperation("EXISTENT_OPERATION", OperationStatus.APPROVED);
        URI uri = new URI(String.format(url, existentOperation.getId()));
        OperationDTO operationDTO = operationDTO("NEW_OPERATION_TYPE", 1.4, OperationStatus.DEPRECATED);

        HttpEntity<OperationDTO> request = new HttpEntity<>(operationDTO);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.PUT, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(OPERATION_TYPE_CANNOT_BE_MODIFIED, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testUpdateOperation() throws URISyntaxException, JsonProcessingException {
        Operation operation = createOperation("EXISTENT_OPERATION", OperationStatus.APPROVED);
        URI uri = new URI(String.format(url, operation.getId()));
        OperationDTO operationDTO = operationDTO(operation.getType(), operation.getCost() + 1.4, OperationStatus.DEPRECATED);

        HttpEntity<OperationDTO> request = new HttpEntity<>(operationDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

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
