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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;

import static com.tncalculator.calculatorapi.configuration.SecurityConfiguration.ADMIN_USER;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;
import static com.tncalculator.calculatorapi.utils.DTOBuilders.operationDTO;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateOperationIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/operations";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testCreateOperationWithEmptyType() throws URISyntaxException {
        OperationDTO operationDTO = operationDTO("", 1.4, OperationStatus.IN_VERIFICATION);
        URI uri = new URI(url);

        HttpEntity<OperationDTO> request = new HttpEntity<>(operationDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(OPERATION_TYPE_NOT_BLANK, new Object[]{});
        assertEquals(expectedMessage, apiError.getErrors().get("type"));
    }

    @Test
    public void testCreateOperationWithoutCost() throws URISyntaxException {
        OperationDTO operationDTO = operationDTO("NEW_OPERATION", 0.0, OperationStatus.IN_VERIFICATION);
        URI uri = new URI(url);

        HttpEntity<OperationDTO> request = new HttpEntity<>(operationDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = "must be greater than 0.0";
        assertEquals(expectedMessage, apiError.getErrors().get("cost"));
    }

    @Test
    public void testCreateOperationWithNullOperationStatus() throws URISyntaxException {
        OperationDTO operationDTO = operationDTO("NEW_OPERATION", 1.0, null);
        URI uri = new URI(url);

        HttpEntity<OperationDTO> request = new HttpEntity<>(operationDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(OPERATION_STATUS_NOT_NULL, new Object[]{});
        assertEquals(expectedMessage, apiError.getErrors().get("status"));
    }

    @Test
    public void testCreateOperationWithInvalidOperationStatus() throws URISyntaxException {
        OperationDTO operationDTO = operationDTO("NEW_OPERATION", 1.0, OperationStatus.APPROVED);
        URI uri = new URI(url);

        HttpEntity<OperationDTO> request = new HttpEntity<>(operationDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(NEW_OPERATION_SHOULD_BE_IN_VERIFICATION, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testCreateOperationWithTypeThatAlreadyExist() throws URISyntaxException {
        Operation existentOperation = createOperation("EXISTENT_OPERATION", OperationStatus.APPROVED);
        OperationDTO operationDTO = operationDTO(existentOperation.getType(), 1.0, OperationStatus.IN_VERIFICATION);
        URI uri = new URI(url);

        HttpEntity<OperationDTO> request = new HttpEntity<>(operationDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(OPERATION_WITH_SAME_TYPE_EXISTS, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testCreateOperation() throws URISyntaxException, JsonProcessingException {
        OperationDTO operationDTO = operationDTO("NEW_OPERATION", 1.0, OperationStatus.IN_VERIFICATION);
        URI uri = new URI(url);

        HttpEntity<OperationDTO> request = new HttpEntity<>(operationDTO);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        assertEquals(HttpStatus.CREATED.value(), result.getStatusCode().value());

        OperationDTO response = getResponse(result.getBody(), OperationDTO.class);
        assertNotNull(response);
        assertAll(
                () -> assertNotEquals(operationDTO.getId(), response.getId()), // a new id is generated
                () -> assertEquals(operationDTO.getType(), response.getType()),
                () -> assertEquals(operationDTO.getCost(), response.getCost()),
                () -> assertEquals(operationDTO.getStatus(), response.getStatus()),
                () -> assertTrue(operationRepository.existsByIdNotDeleted(response.getId()))
        );
    }
}
