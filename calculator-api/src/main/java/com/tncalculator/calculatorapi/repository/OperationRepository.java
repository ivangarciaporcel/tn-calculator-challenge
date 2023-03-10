package com.tncalculator.calculatorapi.repository;

import com.tncalculator.calculatorapi.domain.model.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationRepository extends BaseRepository<Operation, UUID> {

    @Query("SELECT o FROM Operation o WHERE o.id = :id and o.audit.deleted = false")
    Optional<Operation> findByIdNotDeleted(UUID id);

    @Query("SELECT count(o)>0 FROM Operation o WHERE o.id = :id and o.audit.deleted = false")
    boolean existsByIdNotDeleted(UUID id);

    @Query("SELECT o FROM Operation o WHERE o.audit.deleted = false")
    Page<Operation> listNotDeleted(Pageable pageable);
}
