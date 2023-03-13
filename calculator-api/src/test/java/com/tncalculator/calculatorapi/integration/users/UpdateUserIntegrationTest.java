package com.tncalculator.calculatorapi.integration.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.model.Role;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
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
import java.util.stream.Collectors;

import static com.tncalculator.calculatorapi.configuration.SecurityConfiguration.ADMIN_USER;
import static com.tncalculator.calculatorapi.constants.MessageConstants.USERNAME_CANNOT_BE_MODIFIED;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.utils.DTOBuilders.userDTO;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UpdateUserIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/users/%s";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testUpdateUserWithInvalidUUID() throws URISyntaxException {
        URI uri = new URI(String.format(url, "anyId"));
        UserDTO userDTO = userDTO(ADMIN_USER, "anypassword", UserStatus.ACTIVE, 10.0, Set.of(USER_ADMIN));

        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void testUpdateUserNotFound() throws URISyntaxException {
        URI uri = new URI(String.format(url, UUID.randomUUID()));
        UserDTO userDTO = userDTO(ADMIN_USER, "anypassword", UserStatus.ACTIVE, 10.0, Set.of(USER_ADMIN));

        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    public void testUpdateUserChangingUsername() throws URISyntaxException {
        User user = createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        URI uri = new URI(String.format(url, user.getId()));
        UserDTO userDTO = userDTO("otherusername@tn.com", "anypassword", UserStatus.ACTIVE, 10.0, Set.of(USER_ADMIN));

        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<ApiError> result = this.restTemplate.exchange(uri, HttpMethod.PUT, request, ApiError.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());

        ApiError apiError = result.getBody();
        assertNotNull(apiError);
        String expectedMessage = messageService.getMessage(USERNAME_CANNOT_BE_MODIFIED, new Object[]{});
        assertEquals(expectedMessage, apiError.getMessage());
    }

    @Test
    public void testUpdateUser() throws URISyntaxException, JsonProcessingException {
        User user = createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        URI uri = new URI(String.format(url, user.getId()));
        UserDTO userDTO = userDTO(ADMIN_USER, UUID.randomUUID().toString(), UserStatus.INACTIVE, user.getBalance() + 10.0, Set.of(USER_ADMIN));

        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        UserDTO response = getResponse(result.getBody(), UserDTO.class);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(user.getId(), response.getId()),
                () -> assertEquals(user.getUsername(), response.getEmail()),
                () -> assertNotEquals(userDTO.getPassword(), response.getPassword()), // password is now encripted
                () -> assertEquals(userDTO.getStatus(), response.getStatus()), // status was updated
                () -> assertNotEquals(userDTO.getBalance(), response.getBalance()), // balance was modified
                () -> assertEquals(user.getAuthorities().stream().map(Role::getAuthority).collect(Collectors.toSet()),
                        response.getRoles())
        );
    }
}
