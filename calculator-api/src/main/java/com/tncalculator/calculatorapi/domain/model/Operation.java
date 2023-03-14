package com.tncalculator.calculatorapi.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "operations")
@EqualsAndHashCode
@Getter
@Setter
public class Operation implements BaseEntity{

    public static final String FIELD_TYPE = "type";
    public static final String FIELD_OPERATION_STATUS = "status";
    public static final Set<String> FILTER_FIELDS = Set.of(FIELD_TYPE, FIELD_OPERATION_STATUS);

    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "cost", nullable = false)
    private double cost;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationStatus status;

    @Embedded
    private Audit audit = new Audit();

    @Override
    public void markAsDeleted() {
        audit.onDelete();
    }
}
