package com.tncalculator.calculatorapi.integration.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tncalculator.calculatorapi.configuration.SecurityConfiguration;
import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static com.tncalculator.calculatorapi.configuration.SecurityConfiguration.ADMIN_USER;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_CALCULATOR;
import static com.tncalculator.calculatorapi.utils.DTOBuilders.userDTO;
import static com.tncalculator.calculatorapi.utils.EntityBuilders.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateUserIntegrationTest extends BaseIntegrationTest {

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/users";
        setSecurityContextHolder(ADMIN_USER);
    }

    @Test
    public void test() throws NotFoundException {
        User user = user("testUser", Set.of(USER_ADMIN));
        User createdUser = userService.create(user);
        assertNotNull(createdUser);
        User foundUser = userService.findById(createdUser.getId());
        assertNotNull(createdUser);
        assertEquals(createdUser.getId(), foundUser.getId());
    }

    @Test
    public void testCreateUser() throws URISyntaxException, JsonProcessingException {
        UserDTO userDTO = userDTO("test@test.com", "passwordtest",
                UserStatus.ACTIVE, 200.0, Set.of(USER_CALCULATOR));
        URI uri = new URI(baseUrl);

        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        assertEquals(201, result.getStatusCode().value());

        UserDTO response = getResponse(result.getBody(), UserDTO.class);
        assertNotNull(response);
    }
}
