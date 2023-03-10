package com.tncalculator.calculatorapi.services;

import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.dto.UserPartialDTO;
import com.tncalculator.calculatorapi.domain.mapper.UserMapper;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.exceptions.ForbiddenServiceException;
import com.tncalculator.calculatorapi.exceptions.IllegalArgumentServiceException;
import com.tncalculator.calculatorapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseRestService<User, UserDTO, UserPartialDTO> {

    @Autowired
    public UserService(UserRepository userRepository, UserMapper mapper) {
            super(userRepository, mapper, User.class);
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
