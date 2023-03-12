package com.tncalculator.calculatorapi.controllers;

import com.tncalculator.calculatorapi.domain.dto.CalculatorOperationsDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationPartialDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationResultDTO;
import com.tncalculator.calculatorapi.domain.mapper.OperationMapper;
import com.tncalculator.calculatorapi.domain.model.Operation;
import com.tncalculator.calculatorapi.services.OperationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.AuditConstants.CREATED_AT;
import static com.tncalculator.calculatorapi.constants.AuditConstants.UPDATED_AT;
import static com.tncalculator.calculatorapi.constants.MessageConstants.ID_NOT_NULL;
import static com.tncalculator.calculatorapi.domain.model.Operation.FIELD_TYPE;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.utils.PageUtils.getSortOrders;

@RestController
@RequestMapping(path = "/operations")
public class OperationController extends BaseController<Operation, OperationDTO, OperationPartialDTO> {

    private final OperationService operationService;

    private final OperationMapper mapper;

    @Autowired
    public OperationController(OperationService operationService, OperationMapper mapper) {
        super(operationService, mapper);
        this.operationService = operationService;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED)
    @Override
    public OperationDTO create(@RequestBody @Valid OperationDTO dto) {
        return super.create(dto);
    }

    @SneakyThrows
    @GetMapping("/{id}")
    @Override
    public OperationDTO get(@PathVariable Optional<UUID> id) {
        return super.get(id);
    }

    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @PutMapping("/{id}")
    @Override
    public OperationDTO update(@PathVariable Optional<UUID> id, @RequestBody @Valid OperationDTO dto) {
        return super.update(id, dto);
    }

    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @PatchMapping("/{id}")
    @Override
    public OperationDTO patch(@PathVariable Optional<UUID> id, @RequestBody @Valid OperationPartialDTO partialDTO) {
        return super.patch(id, partialDTO);
    }

    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Optional<UUID> id) {
        super.delete(id);
    }

    @SneakyThrows
    @GetMapping
    @Override
    public Page<OperationDTO> list(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(defaultValue = "type,asc") String[] sort) {
        List<Sort.Order> orders = getSortOrders(sort, List.of(FIELD_TYPE, CREATED_AT, UPDATED_AT));
        Pageable pagination = PageRequest.of(page, size, Sort.by(orders));
        Page<Operation> paged = operationService.list(pagination);
        return new PageImpl<>(mapper.entitiesToDTOs(paged.getContent()), paged.getPageable(), paged.getContent().size());
    }

    @SneakyThrows
    @PostMapping("/{id}/calculate")
    public OperationResultDTO calculateOperation(@PathVariable Optional<UUID> id, @RequestBody @Valid CalculatorOperationsDTO dto) {
        checkArgument(id.isPresent(), ID_NOT_NULL);
        return operationService.calculate(id.get(), dto.getParameters());
    }
}
