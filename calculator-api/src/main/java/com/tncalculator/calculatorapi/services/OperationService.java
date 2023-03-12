package com.tncalculator.calculatorapi.services;

import com.tncalculator.calculatorapi.domain.dto.OperationDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationPartialDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationResultDTO;
import com.tncalculator.calculatorapi.domain.mapper.OperationMapper;
import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.domain.model.*;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.operations.CalculatorOperation;
import com.tncalculator.calculatorapi.operations.OperationsFactory;
import com.tncalculator.calculatorapi.repository.OperationRepository;
import com.tncalculator.calculatorapi.repository.RecordRepository;
import com.tncalculator.calculatorapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;
import static com.tncalculator.calculatorapi.security.SecurityUtils.getAuthUserDetails;

@Log4j2
@Service
public class OperationService extends BaseRestService<Operation, OperationDTO, OperationPartialDTO> {

    private final OperationRepository operationRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    @Autowired
    public OperationService(OperationRepository operationRepository, RecordRepository recordRepository,
                            UserRepository userRepository, OperationMapper operationMapper) {
        super(operationRepository, operationMapper, Operation.class);
        this.operationRepository = operationRepository;
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void validateCreate(Operation operation) {
        checkArgument(OperationStatus.IN_VERIFICATION.equals(operation.getStatus()), NEW_OPERATION_SHOULD_BE_IN_VERIFICATION);
    }

    @SneakyThrows
    @Override
    protected void validateUpdate(Operation operation, Operation existentEntity) {
        checkArgument(operation.getType().equals(existentEntity.getType()), OPERATION_TYPE_CANNOT_BE_MODIFIED);
    }

    @Override
    protected void validatePatch(Operation existentEntity, OperationPartialDTO partial) {

    }

    @Override
    protected void validateDelete(Operation operation) {

    }

    @Transactional
    public OperationResultDTO calculate(UUID operationId, Map<String, Double> parameters) throws Exception {
        // Validate operation
        Operation operation = operationRepository.findByIdNotDeleted(operationId)
                .orElseThrow(() -> new NotFoundException(OPERATION_NOT_FOUND, new Object[]{Operation.class.getSimpleName(), operationId}));
        checkArgument(!OperationStatus.DEPRECATED.equals(operation.getStatus()), OPERATION_DEPRECATED);
        checkArgument(!OperationStatus.IN_VERIFICATION.equals(operation.getStatus()), OPERATION_IN_VERIFICATION);

        // Check balance of current user
        UserDetails userDetails = getAuthUserDetails();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUsernameAndUserStatus(userName, UserStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(CURRENT_USER_NOT_FOUND, new Object[]{User.class.getSimpleName(), userName}));

        OperationResponse operationResponse = OperationResponse.APPROVED;
        if (user.getBalance() < operation.getCost()) {
            log.warn(String.format("User %s does not have enough balance to perform operation %s", userName, operationId));
            operationResponse = OperationResponse.DENIED;
        }

        Object result = null;
        double currentUserBalance = user.getBalance();
        if(OperationResponse.APPROVED.equals(operationResponse)) {
            CalculatorOperation<?> calculatorOperation = OperationsFactory.getOperation(operation.getType());
            result = calculatorOperation.calculate(parameters);

            // Update user's balance
            currentUserBalance = user.getBalance() - operation.getCost();
            user.setBalance(currentUserBalance);
            userRepository.save(user);
        }

        // Create historical record
        Record record = record(operation, user, operation.getCost(), currentUserBalance, operationResponse);
        recordRepository.save(record);

        if(OperationResponse.DENIED.equals(operationResponse)) {
            throw new IllegalArgumentException(USER_BALANCE_NOT_ENOUGH_OPERATION);
        }

        return OperationResultDTO.builder()
                .result(result!=null ? result : "")
                .build();
    }

    private Record record(Operation operation, User user, double amount, double userBalance, OperationResponse operationResponse) {
        Record record = new Record();
        record.setOperation(operation);
        record.setUser(user);
        record.setAmount(amount);
        record.setUserBalance(userBalance);
        record.setOperationResponse(operationResponse);
        return record;
    }
}
