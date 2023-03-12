package com.tncalculator.calculatorapi.repository;

import com.tncalculator.calculatorapi.domain.model.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID>, JpaSpecificationExecutor<Record> {

    @Query("SELECT r FROM Record r WHERE r.audit.deleted = false")
    Page<Record> listNotDeleted(Pageable pageable);
}
