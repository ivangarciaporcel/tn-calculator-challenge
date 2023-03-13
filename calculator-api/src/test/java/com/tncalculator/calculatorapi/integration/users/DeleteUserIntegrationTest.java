package com.tncalculator.calculatorapi.integration.users;

import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.model.Role;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeleteUserIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/users/%s";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testDeleteUserWithInvalidUUID() throws URISyntaxException {
        URI uri = new URI(String.format(url, "anyId"));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void testDeleteUserNotFound() throws URISyntaxException {
        URI uri = new URI(String.format(url, UUID.randomUUID()));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    public void testDeleteCurrentUserNotFound() throws URISyntaxException {
        assertFalse(userRepository.existsByUsernameNotDeleted(ADMIN_USER));
        User user = createUser("newuser@tncalculator.com", Set.of(Role.USER_ADMIN));
        URI uri = new URI(String.format(url, user.getId()));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    public void testDeleteUserTryingToDeleteHimself() throws URISyntaxException {
        User user = createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        URI uri = new URI(String.format(url, user.getId()));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getStatusCode().value());
    }

    @Test
    public void testDeleteUser() throws URISyntaxException, NotFoundException {
        User user = createUser("newuser@tncalculator.com", Set.of(Role.USER_ADMIN));
        createUser(ADMIN_USER, Set.of(Role.USER_ADMIN)); // logged user

        URI uri = new URI(String.format(url, user.getId()));

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        assertFalse(userRepository.existsByIdNotDeleted(user.getId()));
    }
}
