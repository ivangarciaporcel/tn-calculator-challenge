package com.tncalculator.calculatorapi.utils;

import com.tncalculator.calculatorapi.domain.model.Authority;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.security.Roles;
import org.checkerframework.checker.units.qual.A;

import java.util.UUID;

public class Builders {

    private Builders() {}

    public static User user(String userName) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(userName);
        user.setPassword(UUID.randomUUID().toString());
        user.setUserStatus(UserStatus.ACTIVE);
        user.setBalance(0.0);
        return user;
    }

    public static Authority authority(Roles role, User user) {
        Authority authority = new Authority();
        authority.setId(UUID.randomUUID());
        authority.setRole(role);
        authority.setUser(user);
        return authority;
    }
}
