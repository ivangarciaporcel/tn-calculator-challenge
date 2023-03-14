package com.tncalculator.calculatorapi.integration.records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.RecordDTO;
import com.tncalculator.calculatorapi.domain.model.Operation;
import com.tncalculator.calculatorapi.domain.model.OperationResponse;
import com.tncalculator.calculatorapi.domain.model.OperationStatus;
import com.tncalculator.calculatorapi.domain.model.User;
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
import java.util.Set;

import static com.tncalculator.calculatorapi.configuration.SecurityConfiguration.ADMIN_USER;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ListRecordIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/records";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testGetRecords() throws URISyntaxException, JsonProcessingException {
        Operation operation = createOperation("EXISTENT_OPERATION", OperationStatus.APPROVED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation, user);
        createRecord(operation, user);

        URI uri = new URI(url);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<RecordDTO> response = getResponseAsPage(result.getBody());
        assertEquals(2, response.getContent().size());
    }

    @Test
    public void testGetRecordsWithPagination() throws URISyntaxException, JsonProcessingException {
        Operation operation = createOperation("EXISTENT_OPERATION", OperationStatus.APPROVED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation, user);
        createRecord(operation, user);

        URI uri = new URI(String.format(url + "?size=1"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<RecordDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetRecordsWithPaginationSize() throws URISyntaxException, JsonProcessingException {
        Operation operation = createOperation("EXISTENT_OPERATION", OperationStatus.APPROVED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation, user);
        createRecord(operation, user);

        URI uri = new URI(String.format(url + "?size=2&page=1"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<RecordDTO> response = getResponseAsPage(result.getBody());
        assertEquals(0, response.getContent().size());
    }

    @Test
    public void testGetRecordsWithInvalidSort() throws URISyntaxException {
        Operation operation = createOperation("EXISTENT_OPERATION", OperationStatus.APPROVED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation, user);
        createRecord(operation, user);

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
    public void testGetRecordsWithMultiSort() throws URISyntaxException, JsonProcessingException {
        Operation operation = createOperation("EXISTENT_OPERATION", OperationStatus.APPROVED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation, user);
        createRecord(operation, user);

        URI uri = new URI(String.format(url + "?sort=user.id,asc&sort=createdAt,desc"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<RecordDTO> response = getResponseAsPage(result.getBody());
        assertEquals(2, response.getContent().size());
    }

    @Test
    public void testGetRecordsWithOperationTypeFilter() throws URISyntaxException, JsonProcessingException {
        Operation operation1 = createOperation("ADD_OPERATION", OperationStatus.APPROVED);
        Operation operation2 = createOperation("MULTIPLY_OPERATION", OperationStatus.APPROVED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation1, user);
        createRecord(operation2, user);

        URI uri = new URI(String.format(url + "?filter=operationType,ADD"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<RecordDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetRecordsWithOperationResponseFilter() throws URISyntaxException, JsonProcessingException {
        Operation operation1 = createOperation("ADD_OPERATION", OperationStatus.APPROVED);
        Operation operation2 = createOperation("MULTIPLY_OPERATION", OperationStatus.DEPRECATED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation1, user, OperationResponse.APPROVED);
        createRecord(operation2, user, OperationResponse.DENIED);

        URI uri = new URI(String.format(url + "?filter=operationResponse,DENIED"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<RecordDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetRecordsWithInvalidOperationResponseFilter() throws URISyntaxException {
        Operation operation1 = createOperation("ADD_OPERATION", OperationStatus.APPROVED);
        Operation operation2 = createOperation("MULTIPLY_OPERATION", OperationStatus.DEPRECATED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation1, user, OperationResponse.APPROVED);
        createRecord(operation2, user, OperationResponse.DENIED);

        String invalidOperationResponse = "non_existent";
        URI uri = new URI(String.format(url + "?filter=operationResponse,%s", invalidOperationResponse));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(CANNOT_FIND_VALUE_ENUM, new Object[]{invalidOperationResponse, OperationResponse.class.getSimpleName()});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetRecordsWithOperationTypeAndResponseFilter() throws URISyntaxException, JsonProcessingException {
        Operation operation1 = createOperation("ADD_OPERATION", OperationStatus.APPROVED);
        Operation operation2 = createOperation("MULTIPLY_OPERATION", OperationStatus.DEPRECATED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation1, user, OperationResponse.APPROVED);
        createRecord(operation2, user, OperationResponse.DENIED);

        URI uri = new URI(String.format(url + "?filter=operationType,MULTIPLY&filter=operationResponse,DENIED"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<RecordDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetRecordsWithInvalidFilter() throws URISyntaxException {
        Operation operation1 = createOperation("ADD_OPERATION", OperationStatus.APPROVED);
        Operation operation2 = createOperation("MULTIPLY_OPERATION", OperationStatus.DEPRECATED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation1, user, OperationResponse.APPROVED);
        createRecord(operation2, user, OperationResponse.DENIED);

        URI uri = new URI(String.format(url + "?filter=invalid,anyvalue"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(INVALID_RECORD_FILTERS, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetRecordsWithInvalidFilterFormat() throws URISyntaxException {
        Operation operation1 = createOperation("ADD_OPERATION", OperationStatus.APPROVED);
        Operation operation2 = createOperation("MULTIPLY_OPERATION", OperationStatus.DEPRECATED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation1, user, OperationResponse.APPROVED);
        createRecord(operation2, user, OperationResponse.DENIED);

        URI uri = new URI(String.format(url + "?filter=operationType"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(INVALID_FILTER_FORMAT, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetRecordsWithInvalidMultiFilterFormat() throws URISyntaxException {
        Operation operation1 = createOperation("ADD_OPERATION", OperationStatus.APPROVED);
        Operation operation2 = createOperation("MULTIPLY_OPERATION", OperationStatus.DEPRECATED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        createRecord(operation1, user, OperationResponse.APPROVED);
        createRecord(operation2, user, OperationResponse.DENIED);

        URI uri = new URI(String.format(url + "?filter=operationType,ADD&filter=operationResponse"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(INVALID_FILTER_FORMAT, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }
}