package com.tncalculator.calculatorapi.configuration;

import com.tncalculator.calculatorapi.domain.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class InMemoryUserDetailsManager implements UserDetailsManager {

    private final Map<String, User> users;

    public InMemoryUserDetailsManager(List<User> usersToAdd) {
        users = new HashMap<>();
        usersToAdd.forEach(this::createUser);
    }

    @Override
    public void createUser(UserDetails user) {
        checkArgument(!userExists(user.getUsername()), "User already exists");
        users.put(user.getUsername(), (User) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        checkArgument(userExists(user.getUsername()), "User does not exist");
        users.put(user.getUsername(), (User) user);
    }

    @Override
    public void deleteUser(String username) {
        users.remove(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.get(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
