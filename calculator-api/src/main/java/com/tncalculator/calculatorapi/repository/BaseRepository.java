package com.tncalculator.calculatorapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<E,I> extends JpaRepository<E, I>, JpaSpecificationExecutor<E> {

    Optional<E> findByIdNotDeleted(I id);

    boolean existsByIdNotDeleted(I id);

    Page<E> listNotDeleted(Pageable pageable);
}
