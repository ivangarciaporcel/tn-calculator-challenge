package com.tncalculator.calculatorapi.security;

import com.google.common.collect.ImmutableList;
import com.tncalculator.calculatorapi.configuration.JwtPropertiesConfiguration;
import com.tncalculator.calculatorapi.exceptions.RestExceptionHandler;
import com.tncalculator.calculatorapi.repository.UserRepository;
import com.tncalculator.calculatorapi.security.filters.JwtAuthenticationFilter;
import com.tncalculator.calculatorapi.security.jwt.JwtTokenDecoder;
import com.tncalculator.calculatorapi.security.jwt.JwtTokenEncoder;
import com.tncalculator.calculatorapi.security.providers.DBAuthenticationProvider;
import com.tncalculator.calculatorapi.security.providers.JwtAuthenticationProvider;
import com.tncalculator.calculatorapi.security.services.DbUserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@ConditionalOnProperty("calculator.security.enabled")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final UserRepository userRepository;

    private final RestExceptionHandler restExceptionHandler;

    private final JwtPropertiesConfiguration jwtPropertiesConfiguration;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserRepository userRepository, RestExceptionHandler restExceptionHandler,
                          JwtPropertiesConfiguration jwtPropertiesConfiguration, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restExceptionHandler = restExceptionHandler;
        this.jwtPropertiesConfiguration = jwtPropertiesConfiguration;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new DbUserDetailsServiceImpl(userRepository);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DBAuthenticationProvider dbAuthenticationProvider = new DBAuthenticationProvider();
        dbAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        dbAuthenticationProvider.setUserDetailsService(userDetailsService());
        return dbAuthenticationProvider;
    }

    @Bean
    public JwtTokenDecoder jwtTokenDecoder() {
        return new JwtTokenDecoder(jwtPropertiesConfiguration);
    }

    @Bean
    public JwtTokenEncoder jwtTokenEncoder() {
        return new JwtTokenEncoder(jwtPropertiesConfiguration);
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtTokenDecoder());
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter(authenticationManager, restExceptionHandler);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(jwtAuthenticationProvider())
                .authenticationProvider(authenticationProvider())
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.authenticationProvider(jwtAuthenticationProvider());
        http.authenticationProvider(authenticationProvider());

        // Add authentication filters
        http.addFilterBefore(jwtAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);

        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();

        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

        // Set unauthorized requests exception handler
        http = http
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                )
                .and();

        // Set permissions on endpoints
        http.authorizeHttpRequests(
                authorize -> authorize.requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
        );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(ImmutableList.of("*"));
        configuration.setAllowedMethods(ImmutableList.of("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}

