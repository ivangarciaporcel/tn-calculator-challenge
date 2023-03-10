package com.tncalculator.calculatorapi.integration.users;

import com.tncalculator.calculatorapi.domain.model.Authority;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.integration.BaseIntegrationTest;
import com.tncalculator.calculatorapi.security.Roles;
import com.tncalculator.calculatorapi.utils.Builders;
import com.tncalculator.calculatorapi.utils.SecurityUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.tncalculator.calculatorapi.utils.Builders.authority;
import static com.tncalculator.calculatorapi.utils.Builders.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
classes = SecurityUtils.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateUserIntegrationTest extends BaseIntegrationTest {

    private String baseUrl;

    @Before
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/users";
    }

    @Test
    @WithUserDetails("admin")
    public void test() throws NotFoundException {
        User user = user("test");
        User createdUser = userService.create(user);
        assertNotNull(createdUser);
        User foundUser = userService.findById(createdUser.getId());
        assertNotNull(createdUser);
        assertEquals(createdUser.getId(), foundUser.getId());
    }
}
