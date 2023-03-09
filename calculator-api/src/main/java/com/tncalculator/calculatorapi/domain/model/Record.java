package com.tncalculator.calculatorapi.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "records")
@EqualsAndHashCode
@Getter
@Setter
public class Record {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;

    @Column(name = "operation_id")
    private UUID operationId;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "user_balance", nullable = false)
    private double userBalance;

    @Column(name = "operation_response", nullable = false)
    private OperationResponse operationResponse;

    @Embedded
    private Audit audit = new Audit();
}