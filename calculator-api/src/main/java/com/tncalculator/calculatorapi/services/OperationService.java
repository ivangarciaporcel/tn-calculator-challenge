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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;
import static com.tncalculator.calculatorapi.domain.model.Operation.*;
import static com.tncalculator.calculatorapi.security.SecurityUtils.getAuthUserDetails;
import static com.tncalculator.calculatorapi.utils.EnumUtils.valueOf;

@Log4j2
@Service
public class OperationService extends BaseRestService<Operation, OperationDTO, OperationPartialDTO> {

    private final OperationRepository operationRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    private final OperationsFactory operationsFactory;

    @Autowired
    public OperationService(OperationRepository operationRepository, RecordRepository recordRepository,
                            UserRepository userRepository, OperationMapper operationMapper,
                            OperationsFactory operationsFactory) {
        super(operationRepository, operationMapper, Operation.class);
        this.operationRepository = operationRepository;
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
        this.operationsFactory = operationsFactory;
    }

    @SneakyThrows
    @Override
    public Page<Operation> list(Pageable pageable, Map<String, String> filters) {
        if (filters.isEmpty()) {
            return operationRepository.listNotDeleted(pageable);
        }
        Set<String> fieldsToFilter = filters.keySet();
        checkArgument(FILTER_FIELDS.containsAll(fieldsToFilter), INVALID_OPERATION_FILTERS);

        if (fieldsToFilter.containsAll(FILTER_FIELDS)) {
            return operationRepository.listByTypeAndStatusNotDeleted(filters.get(FIELD_TYPE),
                    valueOf(OperationStatus.class, filters.get(FIELD_OPERATION_STATUS)), pageable);
        } else if (fieldsToFilter.contains(FIELD_TYPE)) {
            return operationRepository.listByTypeNotDeleted(filters.get(FIELD_TYPE), pageable);
        } else { // it only contains FIELD_OPERATION_STATUS
            return operationRepository.listByStatusNotDeleted(valueOf(OperationStatus.class, filters.get(FIELD_OPERATION_STATUS)), pageable);
        }
    }

    @Override
    protected void validateCreate(Operation operation) {
        checkArgument(OperationStatus.IN_VERIFICATION.equals(operation.getStatus()), NEW_OPERATION_SHOULD_BE_IN_VERIFICATION);
        checkArgument(!operationRepository.existsByTypeNotDeleted(operation.getType()), OPERATION_WITH_SAME_TYPE_EXISTS);
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
        if (OperationResponse.APPROVED.equals(operationResponse)) {
            CalculatorOperation<?> calculatorOperation = operationsFactory.getOperation(operation.getType());
            result = calculatorOperation.calculate(parameters);

            // Update user's balance
            currentUserBalance = user.getBalance() - operation.getCost();
            user.setBalance(currentUserBalance);
            userRepository.save(user);
        }

        // Create historical record
        Record record = record(operation, user, operation.getCost(), currentUserBalance, operationResponse);
        recordRepository.save(record);

        return OperationResultDTO.builder()
                .result(result != null ? result : "")
                .operationResponse(operationResponse)
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
