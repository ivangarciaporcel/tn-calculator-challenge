package com.tncalculator.calculatorapi.repository;

import com.tncalculator.calculatorapi.domain.model.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID>, JpaSpecificationExecutor<Record> {

    @Query("SELECT r FROM Record r WHERE r.user.id = :userId and r.id = :id and r.audit.deleted = false")
    Optional<Record> findByUserAndId(UUID userId, UUID id);

    @Query("SELECT r FROM Record r WHERE r.user.id = :userId and r.audit.deleted = false")
    Page<Record> listByUser(UUID userId, Pageable pageable);

    @Query("SELECT r FROM Record r WHERE r.user.id = :userId and r.audit.deleted = false")
    List<Record> listByUser(UUID userId);

    @Query("SELECT count(r)>0 FROM Record r WHERE r.id = :id and r.audit.deleted = false")
    boolean existsByIdNotDeleted(UUID id);
}
