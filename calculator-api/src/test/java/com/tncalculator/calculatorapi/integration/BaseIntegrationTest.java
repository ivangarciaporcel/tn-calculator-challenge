package com.tncalculator.calculatorapi.integration;

import com.tncalculator.calculatorapi.domain.mapper.OperationMapper;
import com.tncalculator.calculatorapi.domain.mapper.UserMapper;
import com.tncalculator.calculatorapi.repository.OperationRepository;
import com.tncalculator.calculatorapi.repository.UserRepository;
import com.tncalculator.calculatorapi.services.MessageService;
import com.tncalculator.calculatorapi.services.OperationService;
import com.tncalculator.calculatorapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.tncalculator.calculatorapi.utils.ContainerUtils.postgreSQLContainer;

public abstract class BaseIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected OperationRepository operationRepository;

    @Autowired
    protected UserService userService;

    @Autowired
    protected OperationService operationService;

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected OperationMapper operationMapper;

    @LocalServerPort
    protected int port;

    protected static PostgreSQLContainer postgresqlContainer;

    static {
        postgresqlContainer = postgreSQLContainer();
        if(!postgresqlContainer.isRunning()) {
            postgresqlContainer.start();
        }
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:tc:postgresql:13.7-alpine3.16:///quiz_service");
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

}
