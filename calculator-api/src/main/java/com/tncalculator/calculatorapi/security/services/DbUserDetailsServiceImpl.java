package com.tncalculator.calculatorapi.security.services;

import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DbUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public DbUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userRepository.findByUsernameAndUserStatus(userName,  UserStatus.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", userName)));
    }
}
