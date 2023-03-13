package com.tncalculator.calculatorapi.integration.records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.RecordDTO;
import com.tncalculator.calculatorapi.domain.model.Operation;
import com.tncalculator.calculatorapi.domain.model.OperationStatus;
import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.domain.model.User;
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
import java.util.Set;
import java.util.UUID;

import static com.tncalculator.calculatorapi.configuration.SecurityConfiguration.ADMIN_USER;
import static com.tncalculator.calculatorapi.constants.MessageConstants.CURRENT_USER_NOT_FOUND;
import static com.tncalculator.calculatorapi.constants.MessageConstants.ID_NOT_FOUND;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetRecordIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/records/%s";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testGetRecordByIdWithInvalidUUID() throws URISyntaxException {
        URI uri = new URI(String.format(url, "anyId"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void testGetRecordByIdCurrentUserNotFound() throws URISyntaxException {
        URI uri = new URI(String.format(url, UUID.randomUUID()));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(CURRENT_USER_NOT_FOUND, new Object[]{User.class.getSimpleName(), ADMIN_USER});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetRecordByIdNotFound() throws URISyntaxException {
        createUser(ADMIN_USER, Set.of(USER_ADMIN));
        UUID recordId = UUID.randomUUID();
        URI uri = new URI(String.format(url, recordId));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(ID_NOT_FOUND, new Object[]{Record.class.getSimpleName(), recordId});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetRecordById() throws URISyntaxException, JsonProcessingException {
        Operation operation = createOperation("EXISTENT_OPERATION", OperationStatus.APPROVED);
        User user = createUser(ADMIN_USER, Set.of(USER_ADMIN));
        Record record = createRecord(operation, user);

        URI uri = new URI(String.format(url, record.getId()));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        RecordDTO response = getResponse(result.getBody(), RecordDTO.class);
        assertAll(
                () -> assertEquals(record.getId(), response.getId()),
                () -> assertEquals(operation.getId(), response.getOperation().getId()),
                () -> assertEquals(user.getId(), response.getUser().getId()),
                () -> assertEquals(record.getAmount(), response.getAmount()),
                () -> assertEquals(record.getUserBalance(), response.getUserBalance()),
                () -> assertEquals(record.getOperationResponse(), response.getOperationResponse()),
                () -> assertEquals(record.getAudit().getCreatedAt(), response.getCreatedAt())
        );
    }
}
