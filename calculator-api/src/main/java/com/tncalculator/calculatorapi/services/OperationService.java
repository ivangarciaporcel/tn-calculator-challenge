package com.tncalculator.calculatorapi.services;

import com.tncalculator.calculatorapi.domain.dto.OperationDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationPartialDTO;
import com.tncalculator.calculatorapi.domain.mapper.OperationMapper;
import com.tncalculator.calculatorapi.domain.model.Operation;
import com.tncalculator.calculatorapi.exceptions.ForbiddenServiceException;
import com.tncalculator.calculatorapi.exceptions.IllegalArgumentServiceException;
import com.tncalculator.calculatorapi.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperationService extends BaseRestService<Operation, OperationDTO, OperationPartialDTO> {

    @Autowired
    public OperationService(OperationRepository operationRepository, OperationMapper operationMapper) {
        super(operationRepository, operationMapper, Operation.class);
    }
    @Override
    protected void validateCreate(Operation operation) {

    }

    @Override
    protected void validateUpdate(Operation operation, Operation existentEntity) throws IllegalArgumentServiceException, ForbiddenServiceException {

    }

    @Override
    protected void validatePatch(Operation existentEntity, OperationPartialDTO partial) throws IllegalArgumentServiceException, ForbiddenServiceException {

    }

    @Override
    protected void validateDelete(Operation operation) throws ForbiddenServiceException {

    }
}
