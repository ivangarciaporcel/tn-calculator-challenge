package com.tncalculator.calculatorapi.integration.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.CalculatorOperationsDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationResultDTO;
import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.domain.model.*;
import com.tncalculator.calculatorapi.exceptions.ApiError;
import com.tncalculator.calculatorapi.integration.BaseIntegrationTest;
import com.tncalculator.calculatorapi.operations.AdditionOperation;
import com.tncalculator.calculatorapi.operations.CalculatorOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.tncalculator.calculatorapi.configuration.SecurityConfiguration.ADMIN_USER;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;
import static com.tncalculator.calculatorapi.constants.OperationConstants.*;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.utils.DTOBuilders.calculatorOperationsDTO;
import static com.tncalculator.calculatorapi.utils.EntityBuilders.operation;
import static com.tncalculator.calculatorapi.utils.EntityBuilders.user;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CalculateOperationIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/operations/%s/calculate";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testCalculateOperationByIdWithInvalidUUID() throws URISyntaxException {
        URI uri = new URI(String.format(url, "anyId"));
        CalculatorOperationsDTO calculatorOperationsDTO = calculatorOperationsDTO(Map.of());

        HttpEntity<CalculatorOperationsDTO> request = new HttpEntity<>(calculatorOperationsDTO);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void testCalculateOperationNotFound() throws URISyntaxException {
        URI uri = new URI(String.format(url, UUID.randomUUID()));
        CalculatorOperationsDTO calculatorOperationsDTO = calculatorOperationsDTO(Map.of());

        HttpEntity<CalculatorOperationsDTO> request = new HttpEntity<>(calculatorOperationsDTO);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    public void testCalculateOperationDeprecated() throws URISyntaxException {
        Operation operation = createOperation(ADDITION, OperationStatus.DEPRECATED);
        URI uri = new URI(String.format(url, operation.getId()));
        CalculatorOperationsDTO calculatorOperationsDTO = calculatorOperationsDTO(Map.of());

        HttpEntity<CalculatorOperationsDTO> request = new HttpEntity<>(calculatorOperationsDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(OPERATION_DEPRECATED, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testCalculateOperationInVerification() throws URISyntaxException {
        Operation operation = createOperation(ADDITION, OperationStatus.IN_VERIFICATION);
        URI uri = new URI(String.format(url, operation.getId()));
        CalculatorOperationsDTO calculatorOperationsDTO = calculatorOperationsDTO(Map.of());

        HttpEntity<CalculatorOperationsDTO> request = new HttpEntity<>(calculatorOperationsDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(OPERATION_IN_VERIFICATION, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testCalculateOperationCurrentUserNotFound() throws URISyntaxException {
        assertFalse(userRepository.existsByUsernameNotDeleted(ADMIN_USER));

        Operation operation = createOperation(ADDITION, OperationStatus.APPROVED);
        URI uri = new URI(String.format(url, operation.getId()));
        CalculatorOperationsDTO calculatorOperationsDTO = calculatorOperationsDTO(Map.of());

        HttpEntity<CalculatorOperationsDTO> request = new HttpEntity<>(calculatorOperationsDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(CURRENT_USER_NOT_FOUND, new Object[]{User.class.getSimpleName(), ADMIN_USER});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testCalculateOperationNotImplemented() throws URISyntaxException {
        Operation operation = createOperation("NOT_IMPLEMENTED", OperationStatus.APPROVED);
        createUser(ADMIN_USER, Set.of(USER_ADMIN));

        URI uri = new URI(String.format(url, operation.getId()));
        CalculatorOperationsDTO calculatorOperationsDTO = calculatorOperationsDTO(Map.of());

        HttpEntity<CalculatorOperationsDTO> request = new HttpEntity<>(calculatorOperationsDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(OPERATION_NOT_IMPLEMENTED, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testCalculateOperationAdditionWithoutParameters() throws URISyntaxException {
        Operation operation = createOperation(ADDITION, OperationStatus.APPROVED);
        createUser(ADMIN_USER, Set.of(USER_ADMIN));
        CalculatorOperation<Double> addition = new AdditionOperation();

        URI uri = new URI(String.format(url, operation.getId()));
        CalculatorOperationsDTO calculatorOperationsDTO = calculatorOperationsDTO(Map.of());

        HttpEntity<CalculatorOperationsDTO> request = new HttpEntity<>(calculatorOperationsDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(INVALID_OPERATION_PARAMETERS,
                new Object[]{String.join(",", addition.getRequiredParameters())});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testCalculateOperationAdditionNotEnoughUserBalance() throws URISyntaxException, JsonProcessingException {
        double originalBalance = 100.0;
        double operationCost = 120.0;
        User user = user(ADMIN_USER, Set.of(USER_ADMIN));
        user.setBalance(originalBalance);
        user = userRepository.save(user);

        Operation operation = operation(ADDITION, OperationStatus.APPROVED);
        operation.setCost(operationCost);
        operation = operationRepository.save(operation);
        UUID operationId = operation.getId();

        URI uri = new URI(String.format(url, operation.getId()));
        CalculatorOperationsDTO calculatorOperationsDTO = calculatorOperationsDTO(Map.of(FIRST_NUMBER, 1.0, SECOND_NUMBER, 2.0));

        HttpEntity<CalculatorOperationsDTO> request = new HttpEntity<>(calculatorOperationsDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(USER_BALANCE_NOT_ENOUGH_OPERATION, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());

        // Assert user balance has not changed
        Optional<User> optUser = userRepository.findByIdNotDeleted(user.getId());
        assertTrue(optUser.isPresent());
        user = optUser.get();
        assertEquals(originalBalance, user.getBalance());

        // Assert a record was written for the calculation
        List<Record> record = recordRepository.listByUser(user.getId());
        assertEquals(1, record.size());
        Record savedRecord = record.get(0);
        assertAll(
                () -> assertEquals(operationId, savedRecord.getOperation().getId()),
                () -> assertEquals(operationCost, savedRecord.getAmount()),
                () -> assertEquals(originalBalance, savedRecord.getUserBalance()),
                () -> assertEquals(OperationResponse.DENIED, savedRecord.getOperationResponse())
        );
    }

    @Test
    public void testCalculateOperationAddition() throws URISyntaxException, JsonProcessingException {
        double originalBalance = 100.0;
        double operationCost = 90.0;
        User user = user(ADMIN_USER, Set.of(USER_ADMIN));
        user.setBalance(originalBalance);
        user = userRepository.save(user);

        Operation operation = operation(ADDITION, OperationStatus.APPROVED);
        operation.setCost(operationCost);
        operation = operationRepository.save(operation);
        UUID operationId = operation.getId();

        URI uri = new URI(String.format(url, operation.getId()));
        double firstNumber = 1.0;
        double secondNumber = 2.0;
        CalculatorOperationsDTO calculatorOperationsDTO = calculatorOperationsDTO(Map.of(FIRST_NUMBER, firstNumber, SECOND_NUMBER, secondNumber));

        HttpEntity<CalculatorOperationsDTO> request = new HttpEntity<>(calculatorOperationsDTO);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        OperationResultDTO response = getResponse(result.getBody(), OperationResultDTO.class);
        assertAll(
                () -> assertEquals(firstNumber + secondNumber, response.getResult()),
                () -> assertEquals(OperationResponse.APPROVED, response.getOperationResponse())
        );

        assertBalanceAfterApprovedOperation(user.getId(), originalBalance, operationCost);
        assertRecordAfterApprovedOperation(user.getId(), operationId, originalBalance, operationCost);
    }

    @Test
    public void testCalculateOperationRandomString() throws URISyntaxException, JsonProcessingException {
        double originalBalance = 100.0;
        double operationCost = 90.0;
        User user = user(ADMIN_USER, Set.of(USER_ADMIN));
        user.setBalance(originalBalance);
        user = userRepository.save(user);

        Operation operation = operation(RANDOM_STRING, OperationStatus.APPROVED);
        operation.setCost(operationCost);
        operation = operationRepository.save(operation);
        UUID operationId = operation.getId();

        URI uri = new URI(String.format(url, operation.getId()));
        CalculatorOperationsDTO calculatorOperationsDTO = calculatorOperationsDTO(Map.of());

        HttpEntity<CalculatorOperationsDTO> request = new HttpEntity<>(calculatorOperationsDTO);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        OperationResultDTO response = getResponse(result.getBody(), OperationResultDTO.class);
        assertAll(
                () -> assertTrue(StringUtils.isNotBlank((String) response.getResult())),
                () -> assertEquals(OperationResponse.APPROVED, response.getOperationResponse())
        );

        assertBalanceAfterApprovedOperation(user.getId(), originalBalance, operationCost);
        assertRecordAfterApprovedOperation(user.getId(), operationId, originalBalance, operationCost);
    }

    private void assertBalanceAfterApprovedOperation(UUID userId, double originalBalance, double operationCost) {
        Optional<User> optUser = userRepository.findByIdNotDeleted(userId);
        assertTrue(optUser.isPresent());
        User user = optUser.get();
        assertEquals(originalBalance - operationCost, user.getBalance());
    }

    private void assertRecordAfterApprovedOperation(UUID userId, UUID operationId, double originalBalance, double operationCost) {
        List<Record> record = recordRepository.listByUser(userId);
        assertEquals(1, record.size());
        Record savedRecord = record.get(0);
        assertAll(
                () -> assertEquals(operationId, savedRecord.getOperation().getId()),
                () -> assertEquals(operationCost, savedRecord.getAmount()),
                () -> assertEquals(originalBalance - operationCost, savedRecord.getUserBalance()),
                () -> assertEquals(OperationResponse.APPROVED, savedRecord.getOperationResponse())
        );
    }
}
