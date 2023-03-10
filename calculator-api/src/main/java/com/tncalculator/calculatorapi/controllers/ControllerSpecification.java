package com.tncalculator.calculatorapi.controllers;

import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface ControllerSpecification<T, P> {

    T create(T dto);

    T get(Optional<UUID> id);

    T update(Optional<UUID> id, T dto);

    T patch(Optional<UUID> id, P partial);

    void delete(Optional<UUID> id);

    Page<T> list(int page, int size, String[] sort);
}
