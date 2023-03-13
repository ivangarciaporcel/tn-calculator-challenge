package com.tncalculator.calculatorapi.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tncalculator.calculatorapi.configuration.UserConfigurationProperties;
import com.tncalculator.calculatorapi.domain.mapper.OperationMapper;
import com.tncalculator.calculatorapi.domain.mapper.UserMapper;
import com.tncalculator.calculatorapi.domain.model.Operation;
import com.tncalculator.calculatorapi.domain.model.OperationStatus;
import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.repository.OperationRepository;
import com.tncalculator.calculatorapi.repository.RecordRepository;
import com.tncalculator.calculatorapi.repository.UserRepository;
import com.tncalculator.calculatorapi.services.MessageService;
import com.tncalculator.calculatorapi.services.OperationService;
import com.tncalculator.calculatorapi.services.UserService;
import com.tncalculator.calculatorapi.utils.PageModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.tncalculator.calculatorapi.utils.ContainerUtils.postgreSQLContainer;
import static com.tncalculator.calculatorapi.utils.EntityBuilders.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected OperationRepository operationRepository;

    @Autowired
    protected RecordRepository recordRepository;

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

    @Autowired
    protected UserConfigurationProperties userConfigurationProperties;

    @LocalServerPort
    protected int port;

    protected String baseUrl = "http://localhost:";

    protected static HttpHeaders headers;

    public Authentication authentication;

    public SecurityContext securityContext;

    protected static PostgreSQLContainer postgresqlContainer;

    static {
        postgresqlContainer = postgreSQLContainer();
        if (!postgresqlContainer.isRunning()) {
            postgresqlContainer.start();
        }
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:tc:postgresql:13.7-alpine3.16:///tn_calculator");
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @BeforeAll
    public static void start() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        headers = new HttpHeaders();
    }

    @BeforeEach
    public void cleanDatabase() {
        recordRepository.deleteAll();
        operationRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected <T> T getResponse(String body, Class<T> tClass) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(body, tClass);
    }

    protected <T> List<T> getResponseAsList(String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        return objectMapper.readValue(body, new TypeReference<List<T>>() {
        });
    }

    protected <T> Page<T> getResponseAsPage(String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.registerModule(new PageModule());
        return objectMapper.readValue(body, new TypeReference<Page<T>>() {
        });
    }

    protected void setSecurityContextHolder(String userName) {
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        UserDetails userDetails = new User(userName, "", true, true, true, true, Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    protected com.tncalculator.calculatorapi.domain.model.User createUser(String username, Set<String> roles) {
        return userRepository.save(user(username, roles));
    }

    protected Operation createOperation(String operationType, OperationStatus operationStatus) {
        return operationRepository.save(operation(operationType, operationStatus));
    }

    protected Record createRecord(Operation operation, com.tncalculator.calculatorapi.domain.model.User user) {
        return recordRepository.save(record(operation, user));
    }

}
