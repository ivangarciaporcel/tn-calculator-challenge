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
    private String type;

    @Column(name = "cost", nullable = false)
    private double cost;

    @Column(name = "status", nullable = false)
    private OperationStatus status;

    @Embedded
    private Audit audit = new Audit();
}
