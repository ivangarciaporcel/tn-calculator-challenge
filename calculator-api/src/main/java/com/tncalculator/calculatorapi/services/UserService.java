package com.tncalculator.calculatorapi.services;

import com.tncalculator.calculatorapi.configuration.UserConfigurationProperties;
import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.dto.UserPartialDTO;
import com.tncalculator.calculatorapi.domain.mapper.UserMapper;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;
import static com.tncalculator.calculatorapi.security.SecurityUtils.getAuthUserDetails;

@Service
public class UserService extends BaseRestService<User, UserDTO, UserPartialDTO> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConfigurationProperties userConfigurationProperties;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper mapper, PasswordEncoder passwordEncoder,
                       UserConfigurationProperties userConfigurationProperties) {
        super(userRepository, mapper, User.class);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConfigurationProperties = userConfigurationProperties;
    }

    @Override
    @Transactional
    public User create(User entity) {
        validateCreate(entity);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.setBalance(userConfigurationProperties.getInitialBalance());
        return userRepository.save(entity);
    }

    @SneakyThrows
    @Override
    protected void validateCreate(User user) {
        checkArgument(!userRepository.existsByUsernameNotDeleted(user.getUsername()), USERNAME_ALREADY_EXISTS);
    }

    @SneakyThrows
    @Override
    protected void validateUpdate(User user, User existentEntity) {
        checkArgument(existentEntity.getUsername().equals(user.getUsername()), USERNAME_CANNOT_BE_MODIFIED);
    }

    @Override
    protected void validatePatch(User existentEntity, UserPartialDTO partial) {

    }

    @SneakyThrows
    @Override
    protected void validateDelete(User user) {
        UserDetails userDetails = getAuthUserDetails();
        String userName = userDetails.getUsername();
        User loggedUser = userRepository.findByUsernameAndUserStatus(userName, UserStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(CURRENT_USER_NOT_FOUND, new Object[]{User.class.getSimpleName(), userName}));

        checkArgument(!user.getId().equals(loggedUser.getId()), USER_CANNOT_DELETE_HIMSELF);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameAndUserStatus(username, UserStatus.ACTIVE);
    }
}
