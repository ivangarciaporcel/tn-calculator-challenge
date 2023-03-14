package com.tncalculator.calculatorapi.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@EqualsAndHashCode
@Getter
@Setter
public class User implements UserDetails, BaseEntity {

    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_USER_STATUS = "status";
    public static final Set<String> FILTER_FIELDS = Set.of(FIELD_USERNAME, FIELD_USER_STATUS);

    @Id
    @Column(name = "user_id")
    @GeneratedValue
    private UUID id;

    @Column(name = "username", nullable = false, updatable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "balance", nullable = false)
    private double balance;

    @Column(name = "authorities", nullable = false)
    @ElementCollection
    private Set<Role> authorities = new HashSet<>();

    @Embedded
    private Audit audit = new Audit();

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isUserActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isUserActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isUserActive();
    }

    @Override
    public boolean isEnabled() {
        return isUserActive();
    }

    private boolean isUserActive() {
        return UserStatus.ACTIVE.equals(this.userStatus) && !audit.isDeleted();
    }

    @Override
    public void markAsDeleted() {
        audit.onDelete();
    }
}
