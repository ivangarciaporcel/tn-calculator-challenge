package com.tncalculator.calculatorapi.integration.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.OperationDTO;
import com.tncalculator.calculatorapi.domain.model.OperationStatus;
import com.tncalculator.calculatorapi.exceptions.ApiError;
import com.tncalculator.calculatorapi.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static com.tncalculator.calculatorapi.configuration.SecurityConfiguration.ADMIN_USER;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ListOperationIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/operations";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testGetOperations() throws URISyntaxException, JsonProcessingException {
        createOperation(UUID.randomUUID().toString(), OperationStatus.APPROVED);
        createOperation(UUID.randomUUID().toString(), OperationStatus.IN_VERIFICATION);

        URI uri = new URI(url);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<OperationDTO> response = getResponseAsPage(result.getBody());
        assertEquals(2, response.getContent().size());
    }

    @Test
    public void testGetOperationsWithPagination() throws URISyntaxException, JsonProcessingException {
        createOperation(UUID.randomUUID().toString(), OperationStatus.APPROVED);
        createOperation(UUID.randomUUID().toString(), OperationStatus.IN_VERIFICATION);

        URI uri = new URI(String.format(url + "?size=1"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<OperationDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetOperationsWithPaginationSize() throws URISyntaxException, JsonProcessingException {
        createOperation(UUID.randomUUID().toString(), OperationStatus.APPROVED);
        createOperation(UUID.randomUUID().toString(), OperationStatus.IN_VERIFICATION);

        URI uri = new URI(String.format(url + "?size=2&page=1"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<OperationDTO> response = getResponseAsPage(result.getBody());
        assertEquals(0, response.getContent().size());
    }

    @Test
    public void testGetOperationsWithInvalidSort() throws URISyntaxException, JsonProcessingException {
        createOperation(UUID.randomUUID().toString(), OperationStatus.APPROVED);
        createOperation(UUID.randomUUID().toString(), OperationStatus.IN_VERIFICATION);

        String invalidSortField = "invalidField";
        URI uri = new URI(String.format(url + "?sort=%s,asc", invalidSortField));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(SORT_PROPERTY_NOT_VALID, new Object[]{invalidSortField});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetOperationsWithMultiSort() throws URISyntaxException, JsonProcessingException {
        createOperation(UUID.randomUUID().toString(), OperationStatus.APPROVED);
        createOperation(UUID.randomUUID().toString(), OperationStatus.IN_VERIFICATION);

        URI uri = new URI(String.format(url + "?sort=type,asc&sort=createdAt,desc"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<OperationDTO> response = getResponseAsPage(result.getBody());
        assertEquals(2, response.getContent().size());
    }

    @Test
    public void testGetOperationsWithTypeFilter() throws URISyntaxException, JsonProcessingException {
        createOperation("addition_operation", OperationStatus.APPROVED);
        createOperation("subtraction_operation", OperationStatus.IN_VERIFICATION);

        URI uri = new URI(String.format(url + "?filter=type,addition"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<OperationDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetOperationsWithStatusFilter() throws URISyntaxException, JsonProcessingException {
        createOperation("addition_operation", OperationStatus.APPROVED);
        createOperation("subtraction_operation", OperationStatus.IN_VERIFICATION);

        URI uri = new URI(String.format(url + "?filter=status,in_verification"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<OperationDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetOperationsWithInvalidStatusFilter() throws URISyntaxException, JsonProcessingException {
        createOperation("addition_operation", OperationStatus.APPROVED);
        createOperation("subtraction_operation", OperationStatus.IN_VERIFICATION);

        String invalidOperationStatus = "non_existent";
        URI uri = new URI(String.format(url + "?filter=status,%s", invalidOperationStatus));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(CANNOT_FIND_VALUE_ENUM, new Object[]{invalidOperationStatus, OperationStatus.class.getSimpleName()});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetOperationsWithTypeAndStatusFilter() throws URISyntaxException, JsonProcessingException {
        createOperation("addition_operation", OperationStatus.APPROVED);
        createOperation("subtraction_operation", OperationStatus.IN_VERIFICATION);

        URI uri = new URI(String.format(url + "?filter=type,operation&filter=status,in_verification"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<OperationDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetOperationsWithInvalidFilter() throws URISyntaxException {
        createOperation("addition_operation", OperationStatus.APPROVED);
        createOperation("subtraction_operation", OperationStatus.IN_VERIFICATION);

        URI uri = new URI(String.format(url + "?filter=invalid,in_verification"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(INVALID_OPERATION_FILTERS, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetOperationsWithInvalidFilterFormat() throws URISyntaxException {
        createOperation("addition_operation", OperationStatus.APPROVED);
        createOperation("subtraction_operation", OperationStatus.IN_VERIFICATION);

        URI uri = new URI(String.format(url + "?filter=type"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(INVALID_FILTER_FORMAT, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetOperationsWithInvalidMultiFilterFormat() throws URISyntaxException {
        createOperation("addition_operation", OperationStatus.APPROVED);
        createOperation("subtraction_operation", OperationStatus.IN_VERIFICATION);

        URI uri = new URI(String.format(url + "?filter=type,operation&filter=status"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(INVALID_FILTER_FORMAT, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

}
