package com.tncalculator.calculatorapi.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "operations")
@EqualsAndHashCode
@Getter
@Setter
public class Operation {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;

    @Column(name = "type", nullable = false)
    private String operationType;

    @Column(name = "cost", nullable = false)
    private double cost;

    @Embedded
    private Audit audit = new Audit();
}
