package com.tncalculator.calculatorapi.integration.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.model.Role;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
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
import static com.tncalculator.calculatorapi.configuration.SecurityConfiguration.CALCULATOR_USER;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ListUserIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/users";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testGetUsers() throws URISyntaxException, JsonProcessingException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        URI uri = new URI(url);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<UserDTO> response = getResponseAsPage(result.getBody());
        assertEquals(2, response.getContent().size());
    }

    @Test
    public void testGetUsersWithPagination() throws URISyntaxException, JsonProcessingException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        URI uri = new URI(String.format(url + "?size=1"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<UserDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetUsersWithPaginationSize() throws URISyntaxException, JsonProcessingException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        URI uri = new URI(String.format(url + "?size=2&page=1"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<UserDTO> response = getResponseAsPage(result.getBody());
        assertEquals(0, response.getContent().size());
    }

    @Test
    public void testGetUsersWithInvalidSort() throws URISyntaxException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

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
    public void testGetUsersWithMultiSort() throws URISyntaxException, JsonProcessingException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        URI uri = new URI(String.format(url + "?sort=username,asc&sort=createdAt,desc"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<UserDTO> response = getResponseAsPage(result.getBody());
        assertEquals(2, response.getContent().size());
    }

    @Test
    public void testGetUsersWithUserNameFilter() throws URISyntaxException, JsonProcessingException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        URI uri = new URI(String.format(url + "?filter=username,admin"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<UserDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetUsersWithUserStatusFilter() throws URISyntaxException, JsonProcessingException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        URI uri = new URI(String.format(url + "?filter=status,inactive"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<UserDTO> response = getResponseAsPage(result.getBody());
        assertEquals(0, response.getContent().size());
    }

    @Test
    public void testGetUsersWithInvalidUserStatusFilter() throws URISyntaxException, JsonProcessingException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        String invalidOperationStatus = "invalid_status";
        URI uri = new URI(String.format(url + "?filter=status,%s", invalidOperationStatus));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(CANNOT_FIND_VALUE_ENUM, new Object[]{invalidOperationStatus, UserStatus.class.getSimpleName()});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetUsersWithUsernameAndUserStatusFilter() throws URISyntaxException, JsonProcessingException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        URI uri = new URI(String.format(url + "?filter=status,active&filter=username,user"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Page<UserDTO> response = getResponseAsPage(result.getBody());
        assertEquals(1, response.getContent().size());
    }

    @Test
    public void testGetUsersWithInvalidFilter() throws URISyntaxException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        URI uri = new URI(String.format(url + "?filter=invalidField,admin"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(INVALID_USER_FILTERS, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetUsersWithInvalidFilterFormat() throws URISyntaxException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        URI uri = new URI(String.format(url + "?filter=username"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(INVALID_FILTER_FORMAT, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testGetUsersWithInvalidMultiFilterFormat() throws URISyntaxException {
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        createUser(CALCULATOR_USER, Set.of(Role.USER_CALCULATOR));

        URI uri = new URI(String.format(url + "?filter=username,admin&filter=status"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(INVALID_FILTER_FORMAT, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }
}
