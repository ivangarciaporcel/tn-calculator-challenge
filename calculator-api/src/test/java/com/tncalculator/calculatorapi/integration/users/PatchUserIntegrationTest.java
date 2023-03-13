package com.tncalculator.calculatorapi.integration.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.dto.UserPartialDTO;
import com.tncalculator.calculatorapi.domain.model.Role;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
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
import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_CALCULATOR;
import static com.tncalculator.calculatorapi.utils.DTOBuilders.userPartialDTO;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatchUserIntegrationTest extends BaseIntegrationTest {

    private String url;

    @BeforeEach
    public void setUp() {
        url = baseUrl + port + "/users/%s";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void testPatchUserWithInvalidUUID() throws URISyntaxException {
        URI uri = new URI(String.format(url, "anyId"));
        UserPartialDTO userPartialDTO = userPartialDTO("anypassword", UserStatus.ACTIVE, Set.of(USER_ADMIN));

        HttpEntity<UserPartialDTO> request = new HttpEntity<>(userPartialDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PATCH, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void testPatchUserNotFound() throws URISyntaxException {
        URI uri = new URI(String.format(url, UUID.randomUUID()));
        UserPartialDTO userPartialDTO = userPartialDTO("anypassword", UserStatus.ACTIVE, Set.of(USER_ADMIN));

        HttpEntity<UserPartialDTO> request = new HttpEntity<>(userPartialDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PATCH, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    public void testPatchUser() throws URISyntaxException, JsonProcessingException {
        User user = createUser(ADMIN_USER, Set.of(Role.USER_ADMIN));
        URI uri = new URI(String.format(url, user.getId()));
        UserPartialDTO userPartialDTO = userPartialDTO("anypassword", UserStatus.INACTIVE, Set.of(USER_ADMIN, USER_CALCULATOR));

        HttpEntity<UserPartialDTO> request = new HttpEntity<>(userPartialDTO);
        ResponseEntity<String> result = this.restTemplate.exchange(uri, HttpMethod.PATCH, request, String.class);

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        UserDTO response = getResponse(result.getBody(), UserDTO.class);
        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getEmail());
        assertNotEquals(user.getPassword(), response.getPassword()); // password is now encripted
        assertEquals(userPartialDTO.getStatus().get(), response.getStatus()); // status was updated
        assertEquals(user.getBalance(), response.getBalance()); // balance was not modified
        assertEquals(userPartialDTO.getRoles().get(), response.getRoles());
    }

}
