package com.tncalculator.calculatorapi.unit.services;

import com.tncalculator.calculatorapi.configuration.UserConfigurationProperties;
import com.tncalculator.calculatorapi.domain.dto.UserPartialDTO;
import com.tncalculator.calculatorapi.domain.mapper.UserMapper;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.repository.UserRepository;
import com.tncalculator.calculatorapi.services.UserService;
import com.tncalculator.calculatorapi.unit.SecurityMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.utils.AssertionUtils.assertUser;
import static com.tncalculator.calculatorapi.utils.EntityBuilders.user;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class UserServiceTest extends SecurityMock {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserConfigurationProperties userConfigurationProperties;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userMapper = mock(UserMapper.class);
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userConfigurationProperties = mock(UserConfigurationProperties.class);
        userService = new UserService(userRepository, userMapper, passwordEncoder, userConfigurationProperties);
    }

    @AfterEach
    public void tearDown() {
        reset(userRepository, passwordEncoder, userConfigurationProperties, userMapper);
    }

    @Test
    public void testCreateUserWithUsernameAlreadyTaken() {
        User toCreate = user("test", Set.of(USER_ADMIN));
        when(userRepository.existsByUsernameNotDeleted(toCreate.getUsername())).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> userService.create(toCreate));
    }

    @Test
    public void testCreateUser() {
        User toCreate = user("test", Set.of(USER_ADMIN));
        when(userRepository.existsByUsernameNotDeleted(toCreate.getUsername())).thenReturn(false);
        when(userRepository.save(toCreate)).thenReturn(toCreate);
        User createdUser = userService.create(toCreate);
        assertNotNull(createdUser);
        assertUser(toCreate, createdUser);
    }

    @Test
    public void testFindUserByIdAndNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> userService.findById(userId));
    }

    @Test
    public void testFindUserById() throws Exception {
        UUID userId = UUID.randomUUID();
        User existentUser = user("username", Set.of(USER_ADMIN));
        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.of(existentUser));
        User foundUser = userService.findById(userId);
        assertUser(existentUser, foundUser);
    }

    @Test
    public void testUpdateUserNotFound() {
        UUID userId = UUID.randomUUID();
        User toUpdate = user("test", Set.of(USER_ADMIN));

        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> userService.update(userId, toUpdate));
    }

    @Test
    public void testUpdateUserWithDifferentUsername() {
        UUID userId = UUID.randomUUID();
        User toUpdate = user("another username", Set.of(USER_ADMIN));
        User existentUser = user("username", Set.of(USER_ADMIN));

        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.of(existentUser));
        assertThrows(IllegalArgumentException.class,
                () -> userService.update(userId, toUpdate));
    }

    @Test
    public void testUpdateUser() throws Exception {
        UUID userId = UUID.randomUUID();
        User toUpdate = user("username", Set.of(USER_ADMIN));
        User existentUser = user("username", Set.of(USER_ADMIN));

        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.of(existentUser));
        when(userRepository.save(existentUser)).thenReturn(toUpdate);
        User updatedUser = userService.update(userId, toUpdate);
        assertEquals(toUpdate, updatedUser);
    }

    @Test
    public void testPatchUserNotFound() {
        UUID userId = UUID.randomUUID();
        UserPartialDTO partialDTO = mock(UserPartialDTO.class);

        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> userService.patch(userId, partialDTO));
    }

    @Test
    public void testPatchUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPartialDTO partialDTO = mock(UserPartialDTO.class);
        User existentUser = user("username", Set.of(USER_ADMIN));

        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.of(existentUser));
        when(userRepository.save(existentUser)).thenReturn(existentUser);
        User patchedUser = userService.patch(userId, partialDTO);
        assertEquals(existentUser, patchedUser);
    }

    @Test
    public void testDeleteUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> userService.delete(userId));
    }

    @Test
    public void testDeleteLoggedUserNotFound() {
        UUID userId = UUID.randomUUID();
        User existentUser = user("username", Set.of(USER_ADMIN));

        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.of(existentUser));
        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> userService.delete(userId));
    }

    @Test
    public void testDeleteThatUserCannotDeleteHimself() {
        UUID userId = UUID.randomUUID();
        User existentUser = user("username", Set.of(USER_ADMIN));
        User loggedUser = user(userDetails.getUsername(), Set.of(USER_ADMIN));
        loggedUser.setId(existentUser.getId());

        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.of(existentUser));
        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.of(loggedUser));
        assertThrows(IllegalArgumentException.class,
                () -> userService.delete(userId));
    }

    @Test
    public void testDeleteUser() throws Exception {
        UUID userId = UUID.randomUUID();
        User existentUser = user("username", Set.of(USER_ADMIN));
        User loggedUser = user(userDetails.getUsername(), Set.of(USER_ADMIN));

        when(userRepository.findByIdNotDeleted(userId)).thenReturn(Optional.of(existentUser));
        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.of(loggedUser));
        when(userRepository.save(existentUser)).thenReturn(existentUser);
        userService.delete(userId);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User deletedUser = userCaptor.getValue();
        assertTrue(deletedUser.getAudit().isDeleted());
    }

    @Test
    public void testListUsers() {
        Pageable pageable = mock(Pageable.class);
        Page<User> result = Page.empty();
        when(userRepository.listNotDeleted(pageable)).thenReturn(result);

        Page<User> pagedList = userService.list(pageable, Map.of());
        assertTrue(pagedList.isEmpty());
        verify(userRepository, times(1)).listNotDeleted(pageable);
    }

    @Test
    public void testFindByUsername() {
        User existentUser = user("username", Set.of(USER_ADMIN));
        String username = existentUser.getUsername();

        when(userRepository.findByUsernameAndUserStatus(username, UserStatus.ACTIVE)).thenReturn(Optional.of(existentUser));
        Optional<User> foundUser = userService.findByUsername(username);
        assertTrue(foundUser.isPresent());
        assertUser(existentUser, foundUser.get());
    }
}
