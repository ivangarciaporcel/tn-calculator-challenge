package com.tncalculator.calculatorapi.domain.model;

import com.tncalculator.calculatorapi.security.Roles;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@EqualsAndHashCode
@Getter
@Setter
public class User implements UserDetails, BaseEntity {

    public static final String FIELD_USERNAME = "username";
    @Id
    @Column(name = "user_id")
    @GeneratedValue
    private UUID id;

    @Column(name = "username", nullable = false, updatable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "status", nullable = false)
    private UserStatus userStatus;

    @Column(name = "balance", nullable = false)
    private double balance;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Authority> authorities;

    @Transient
    private List<Roles> roles;

    @Embedded
    private Audit audit = new Audit();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(auth -> new SimpleGrantedAuthority(auth.getRole().getName()))
                .collect(Collectors.toList());
    }

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
        return UserStatus.ACTIVE.equals(this.userStatus);
    }

    @Override
    public void markAsDeleted() {
        audit.onDelete();
    }
}
