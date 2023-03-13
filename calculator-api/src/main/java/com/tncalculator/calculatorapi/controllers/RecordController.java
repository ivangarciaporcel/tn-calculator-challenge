package com.tncalculator.calculatorapi.controllers;

import com.tncalculator.calculatorapi.domain.dto.RecordDTO;
import com.tncalculator.calculatorapi.domain.mapper.RecordMapper;
import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.services.RecordService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.AuditConstants.CREATED_AT;
import static com.tncalculator.calculatorapi.constants.AuditConstants.UPDATED_AT;
import static com.tncalculator.calculatorapi.constants.MessageConstants.ID_NOT_NULL;
import static com.tncalculator.calculatorapi.domain.model.Record.FIELD_OPERATION_ID;
import static com.tncalculator.calculatorapi.domain.model.Record.FIELD_USER_ID;
import static com.tncalculator.calculatorapi.utils.PageUtils.getSortOrders;

@RestController
@RequestMapping("/records")
public class RecordController {

    private final RecordService recordService;

    private final RecordMapper recordMapper;

    @Autowired
    public RecordController(RecordService recordService, RecordMapper recordMapper) {
        this.recordService = recordService;
        this.recordMapper = recordMapper;
    }

    @SneakyThrows
    @GetMapping("/{id}")
    public RecordDTO getById(@PathVariable Optional<UUID> id) {
        checkArgument(id.isPresent(), ID_NOT_NULL);
        return recordMapper.map(recordService.findById(id.get()));
    }

    @SneakyThrows
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Optional<UUID> id) {
        checkArgument(id.isPresent(), ID_NOT_NULL);
        recordService.delete(id.get());
    }

    @SneakyThrows
    @GetMapping
    public Page<RecordDTO> listRecords(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "createdAt,asc") String[] sort) {
        List<Sort.Order> orders = getSortOrders(sort, List.of(FIELD_USER_ID, FIELD_OPERATION_ID, CREATED_AT, UPDATED_AT));
        Pageable pagination = PageRequest.of(page, size, Sort.by(orders));
        Page<Record> paged = recordService.listByCurrentUser(pagination);
        return new PageImpl<>(recordMapper.entitiesToDTOs(paged.getContent()), paged.getPageable(), paged.getContent().size());
    }
}
