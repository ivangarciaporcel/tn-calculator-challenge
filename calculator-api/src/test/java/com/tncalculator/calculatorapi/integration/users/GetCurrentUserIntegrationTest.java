package com.tncalculator.calculatorapi.integration.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.model.Role;
import com.tncalculator.calculatorapi.domain.model.User;
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
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetCurrentUserIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/users/current";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testGetCurrentUserNotFound() throws URISyntaxException {
        assertFalse(userRepository.existsByUsernameNotDeleted(ADMIN_USER));
        URI uri = new URI(url);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    public void testGetCurrentUser() throws URISyntaxException, JsonProcessingException {
        User user = createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));

        URI uri = new URI(url);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        UserDTO response = getResponse(result.getBody(), UserDTO.class);
        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getEmail());
        assertEquals(user.getPassword(), response.getPassword());
        assertEquals(user.getUserStatus(), response.getStatus());
        assertEquals(user.getBalance(), response.getBalance());
        assertEquals(user.getAuthorities().stream().map(Role::getAuthority).collect(Collectors.toSet()),
                response.getRoles());
    }
}
