package com.tncalculator.calculatorapi.services;

import com.tncalculator.calculatorapi.exceptions.ForbiddenServiceException;
import com.tncalculator.calculatorapi.exceptions.IllegalArgumentServiceException;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestService<E, P, I> {

    E create(E e);

    E findById(I id) throws NotFoundException;

    E update(I id, E e) throws NotFoundException, IllegalArgumentServiceException, ForbiddenServiceException;

    E patch(I id, P partial) throws NotFoundException, IllegalArgumentServiceException, ForbiddenServiceException;

    void delete(I id) throws NotFoundException, ForbiddenServiceException;

    Page<E> list(Pageable pageable);
}