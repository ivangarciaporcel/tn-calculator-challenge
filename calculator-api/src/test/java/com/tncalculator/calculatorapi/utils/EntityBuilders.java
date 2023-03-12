package com.tncalculator.calculatorapi.utils;

import com.tncalculator.calculatorapi.domain.model.Role;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.domain.model.UserStatus;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntityBuilders {

    private EntityBuilders() {}

    public static User user(String userName, Set<String> roles) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(userName);
        user.setPassword(UUID.randomUUID().toString());
        user.setUserStatus(UserStatus.ACTIVE);
        user.setBalance(0.0);
        Set<Role> roleSet = roles.stream().map(Role::new).collect(Collectors.toSet());
        user.setAuthorities(roleSet);
        return user;
    }

}
