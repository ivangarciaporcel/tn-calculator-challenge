package com.tncalculator.calculatorapi.domain.mapper;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class CommonMapper {

    public <T> T unwrap(Optional<T> optional) {
        if (optional != null) {
            return optional.orElse(null);
        }
        return null;
    }

}
