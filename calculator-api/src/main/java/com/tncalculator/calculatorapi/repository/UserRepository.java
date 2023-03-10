package com.tncalculator.calculatorapi.repository;

import com.tncalculator.calculatorapi.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE u.id = :id and u.audit.deleted = false")
    Optional<User> findByIdNotDeleted(UUID id);

    @Query("SELECT count(u)>0 FROM User u WHERE u.id = :id and u.audit.deleted = false")
    boolean existsByIdNotDeleted(UUID id);

    @Query("SELECT u FROM User u WHERE u.audit.deleted = false")
    Page<User> listNotDeleted(Pageable pageable);
}
