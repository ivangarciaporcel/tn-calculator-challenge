package com.tncalculator.calculatorapi.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "records")
@EqualsAndHashCode
@Getter
@Setter
public class Record implements BaseEntity {

    public static final String FIELD_USER_ID = "user.id";
    public static final String FIELD_OPERATION_ID = "operation.id";
    public static final String FIELD_OPERATION_TYPE = "operationType";
    public static final String FIELD_OPERATION_RESPONSE = "operationResponse";

    public static final Set<String> FILTER_FIELDS = Set.of(FIELD_OPERATION_TYPE, FIELD_OPERATION_RESPONSE);
    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;

    @JoinColumn(name = "operation_id")
    @ManyToOne
    private Operation operation;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "user_balance", nullable = false)
    private double userBalance;

    @Column(name = "operation_response", nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationResponse operationResponse;

    @Embedded
    private Audit audit = new Audit();

    @Override
    public void markAsDeleted() {
        audit.onDelete();
    }
}
