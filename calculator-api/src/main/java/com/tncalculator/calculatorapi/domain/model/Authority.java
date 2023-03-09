package com.tncalculator.calculatorapi.domain.model;

import com.tncalculator.calculatorapi.security.Roles;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "authorities")
@EqualsAndHashCode
@Getter
@Setter
public class Authority {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;

    @Column(name = "role")
    private Roles role;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Embedded
    private Audit audit = new Audit();
}
