package com.tncalculator.calculatorapi.repository;

import com.tncalculator.calculatorapi.domain.model.Operation;
import com.tncalculator.calculatorapi.domain.model.OperationStatus;
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

    @Query("SELECT count(o)>0 FROM Operation o WHERE o.type = :type and o.audit.deleted = false")
    boolean existsByTypeNotDeleted(String type);

    @Query("SELECT o FROM Operation o WHERE o.audit.deleted = false")
    Page<Operation> listNotDeleted(Pageable pageable);

    @Query("SELECT o FROM Operation o WHERE o.type LIKE %:type% and o.audit.deleted = false")
    Page<Operation> listByTypeNotDeleted(String type, Pageable pageable);

    @Query("SELECT o FROM Operation o WHERE o.status = :status and o.audit.deleted = false")
    Page<Operation> listByStatusNotDeleted(OperationStatus status, Pageable pageable);

    @Query("SELECT o FROM Operation o WHERE o.type LIKE %:type% and o.status = :status and o.audit.deleted = false")
    Page<Operation> listByTypeAndStatusNotDeleted(String type, OperationStatus status, Pageable pageable);
}
