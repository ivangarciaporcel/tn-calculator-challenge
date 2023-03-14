package com.tncalculator.calculatorapi.controllers;

import com.tncalculator.calculatorapi.domain.dto.AuthRequestDTO;
import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.mapper.UserMapper;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.security.jwt.JwtTokenEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnProperty("calculator.security.enabled")
@RestController
@RequestMapping(path = "/login")
@Tag(name = "Login", description = "Rest API that provides user login capabilities")
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

    @Operation(summary = "Validate user credentials and generate a jwt token returned in the header")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User validated and jwt token generated", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserDTO> login(@Parameter(description = "Representation of the user credentials to be validated")
                                         @RequestBody @Valid AuthRequestDTO authRequestDTO) {
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
