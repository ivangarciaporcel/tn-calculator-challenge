package com.tncalculator.calculatorapi.utils;

import com.tncalculator.calculatorapi.domain.model.*;
import com.tncalculator.calculatorapi.domain.model.Record;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntityBuilders {

    private EntityBuilders() {}

    public static User user(String userName, Set<String> roles) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(userName);
        user.setPassword(UUID.randomUUID().toString());
        user.setUserStatus(UserStatus.ACTIVE);
        user.setBalance(100.0);
        Set<Role> roleSet = roles.stream().map(Role::new).collect(Collectors.toSet());
        user.setAuthorities(roleSet);
        return user;
    }

    public static Operation operation(String operationType, OperationStatus operationStatus) {
        Operation operation = new Operation();
        operation.setId(UUID.randomUUID());
        operation.setType(operationType);
        operation.setCost(100.0);
        operation.setStatus(operationStatus);
        return operation;
    }

    public static Record record(Operation operation, User user, double amount, double userBalance, OperationResponse operationResponse) {
        Record record = new Record();
        record.setOperation(operation);
        record.setUser(user);
        record.setAmount(amount);
        record.setUserBalance(userBalance);
        record.setOperationResponse(operationResponse);
        return record;
    }

    public static Record record(Operation operation, User user) {
        Record record = new Record();
        record.setOperation(operation);
        record.setUser(user);
        record.setAmount(100.0);
        record.setUserBalance(20.0);
        record.setOperationResponse(OperationResponse.APPROVED);
        return record;
    }

    public static Record record(Operation operation, User user, OperationResponse operationResponse) {
        Record record = new Record();
        record.setOperation(operation);
        record.setUser(user);
        record.setAmount(100.0);
        record.setUserBalance(20.0);
        record.setOperationResponse(operationResponse);
        return record;
    }

}
