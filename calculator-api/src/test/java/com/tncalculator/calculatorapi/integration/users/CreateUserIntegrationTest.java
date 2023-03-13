package com.tncalculator.calculatorapi.integration.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.exceptions.ApiError;
import com.tncalculator.calculatorapi.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static com.tncalculator.calculatorapi.configuration.SecurityConfiguration.ADMIN_USER;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_CALCULATOR;
import static com.tncalculator.calculatorapi.utils.DTOBuilders.userDTO;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateUserIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/users";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws URISyntaxException {
        UserDTO userDTO = userDTO("invalid_email", "passwordtest",
                UserStatus.ACTIVE, 200.0, Set.of(USER_CALCULATOR));
        URI uri = new URI(url);

        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(EMAIL_NOT_VALID, new Object[]{});
        assertEquals(expectedMessage, apiError.getErrors().get("email"));
    }

    @Test
    public void testCreateUserWithBlankPassword() throws URISyntaxException {
        UserDTO userDTO = userDTO("test@test.com", "",
                UserStatus.ACTIVE, 200.0, Set.of(USER_CALCULATOR));
        URI uri = new URI(url);

        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(PASSWORD_NOT_BLANK, new Object[]{});
        assertEquals(expectedMessage, apiError.getErrors().get("password"));
    }

    @Test
    public void testCreateUserWithNullUserstatus() throws URISyntaxException {
        UserDTO userDTO = userDTO("test@test.com", "anypassword", null, 200.0, Set.of(USER_CALCULATOR));
        URI uri = new URI(url);

        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(USER_STATUS_NOT_NULL, new Object[]{});
        assertEquals(expectedMessage, apiError.getErrors().get("status"));
    }

    private Stream<Set<String>> provideInvalidRoles() {
        return Stream.of(
                null,
                Collections.emptySet(),
                Set.of("INVALID_ROLE")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRoles")
    public void testCreateUserWithInvalidRoles(Set<String> roles) throws URISyntaxException {
        UserDTO userDTO = userDTO("test@test.com", "anypassword", UserStatus.ACTIVE, 200.0, roles);
        URI uri = new URI(url);

        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<ApiError> result = this.restTemplate.postForEntity(uri, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        assertEquals("There is one or more invalid roles", apiError.getErrors().get("roles"));
    }

    @Test
    public void testCreateUser() throws URISyntaxException, JsonProcessingException {
        UserDTO userDTO = userDTO("test@test.com", "anypassword",
                UserStatus.ACTIVE, 200.0, Set.of(USER_CALCULATOR));
        URI uri = new URI(url);

        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        assertEquals(HttpStatus.CREATED.value(), result.getStatusCode().value());

        UserDTO response = getResponse(result.getBody(), UserDTO.class);
        assertNotNull(response);

        assertNotEquals(userDTO.getId(), response.getId()); // new id is generated
        assertEquals(userDTO.getEmail(), response.getEmail());
        assertNotEquals(userDTO.getPassword(), response.getPassword()); // password is now encrypted
        assertEquals(userDTO.getStatus(), response.getStatus());
        assertEquals(userConfigurationProperties.getInitialBalance(), response.getBalance()); // balance should be the configured one
        assertEquals(userDTO.getRoles(), response.getRoles());

        assertTrue(userRepository.existsByIdNotDeleted(response.getId()));
    }
}
