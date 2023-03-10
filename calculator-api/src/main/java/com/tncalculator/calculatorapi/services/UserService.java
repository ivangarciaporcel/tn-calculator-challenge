package com.tncalculator.calculatorapi.services;

import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.dto.UserPartialDTO;
import com.tncalculator.calculatorapi.domain.mapper.UserMapper;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.exceptions.ForbiddenServiceException;
import com.tncalculator.calculatorapi.exceptions.IllegalArgumentServiceException;
import com.tncalculator.calculatorapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseRestService<User, UserDTO, UserPartialDTO> {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper mapper, PasswordEncoder passwordEncoder) {
            super(userRepository, mapper, User.class);
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User create(User entity) {
        validateCreate(entity);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        User createdUser = userRepository.save(entity);
        return createdUser;
    }

    @Override
    protected void validateCreate(User user) {

    }

    @Override
    protected void validateUpdate(User user, User existentEntity) throws IllegalArgumentServiceException, ForbiddenServiceException {

    }

    @Override
    protected void validatePatch(User existentEntity, UserPartialDTO partial) throws IllegalArgumentServiceException, ForbiddenServiceException {

    }

    @Override
    protected void validateDelete(User user) throws ForbiddenServiceException {

    }
}
