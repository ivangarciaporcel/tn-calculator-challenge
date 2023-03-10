package com.tncalculator.calculatorapi.controllers;

import com.tncalculator.calculatorapi.domain.dto.AuthRequestDTO;
import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.mapper.UserMapper;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.security.jwt.JwtTokenEncoder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/login")
public class AuthApi {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final JwtTokenEncoder jwtTokenEncoder;

    @Autowired
    public AuthApi(AuthenticationManager authenticationManager, UserMapper userMapper, JwtTokenEncoder jwtTokenEncoder) {
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.jwtTokenEncoder = jwtTokenEncoder;
    }

    @PostMapping
    public ResponseEntity<UserDTO> login(@RequestBody @Valid AuthRequestDTO authRequestDTO) {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestDTO.getUsername(), authRequestDTO.getPassword()
                    )
            );
            User user = (User) authenticate.getPrincipal();

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            jwtTokenEncoder.encode(user)
                    )
                    .body(userMapper.entityToDTO(user));
    }
}
