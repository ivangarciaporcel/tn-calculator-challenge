package com.tncalculator.calculatorapi.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.tncalculator.calculatorapi.security.SecurityUtils.getAuthUserDetails;

@EqualsAndHashCode
@Getter
@Setter
@Embeddable
public class Audit {

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted")
    private boolean deleted;

    @PrePersist
    public void prePersist() {
        UserDetails user = getAuthUserDetails();
        createdAt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        createdBy = user.getUsername();
        deleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        UserDetails user = getAuthUserDetails();
        updatedBy = user.getUsername();
    }

    public void onDelete() {
        updatedAt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        UserDetails user = getAuthUserDetails();
        updatedBy = user.getUsername();
        deleted = true;
    }
}
