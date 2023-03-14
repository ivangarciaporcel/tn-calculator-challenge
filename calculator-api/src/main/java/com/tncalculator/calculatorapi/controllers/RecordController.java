package com.tncalculator.calculatorapi.controllers;

import com.tncalculator.calculatorapi.domain.dto.RecordDTO;
import com.tncalculator.calculatorapi.domain.mapper.RecordMapper;
import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.services.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import static com.tncalculator.calculatorapi.domain.model.Record.*;
import static com.tncalculator.calculatorapi.utils.PageUtils.getFilters;
import static com.tncalculator.calculatorapi.utils.PageUtils.getSortOrders;

@RestController
@RequestMapping("/records")
@Tag(name = "Records", description = "Rest API that provides record CRUD capabilities")
public class RecordController {

    private final RecordService recordService;

    private final RecordMapper recordMapper;

    @Autowired
    public RecordController(RecordService recordService, RecordMapper recordMapper) {
        this.recordService = recordService;
        this.recordMapper = recordMapper;
    }

    @Operation(summary = "Get a Record given an id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record was found", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = RecordDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @SneakyThrows
    @GetMapping("/{id}")
    public RecordDTO getById(@Parameter(description = "Id of the Record to be searched") @PathVariable Optional<UUID> id) {
        checkArgument(id.isPresent(), ID_NOT_NULL);
        return recordMapper.map(recordService.findById(id.get()));
    }

    @Operation(summary = "Delete a Record given an id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record was deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
    })
    @SneakyThrows
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "Id of the Record to be deleted") @PathVariable Optional<UUID> id) {
        checkArgument(id.isPresent(), ID_NOT_NULL);
        recordService.delete(id.get());
    }

    @Operation(summary = "List records with sorting and filter capabilities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listed records that match the search criteria", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Page.class)))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
    })
    @SneakyThrows
    @GetMapping
    public Page<RecordDTO> listRecords(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       @Parameter(description = "Sort fields separated by a comma, i.e. sort=user.username,asc&sort=createdAt,desc. Accepted sort fields: user.username, operation.type, createdAt and updatedAt")
                                       @RequestParam(defaultValue = "createdAt,asc") String[] sort,
                                       @Parameter(description = "Filter fields separated by a comma, i.e. filter=operationType,ADDITION&filter=operationResponse,APPROVED. Accepted filter fields: type and status (APPROVED, DENIED)")
                                       @RequestParam(defaultValue = "") String[] filter) {
        List<Sort.Order> orders = getSortOrders(sort, List.of(SORT_FIELD_OPERATION_STATUS, SORT_FIELD_OPERATION_TYPE, CREATED_AT, UPDATED_AT));
        Pageable pagination = PageRequest.of(page, size, Sort.by(orders));
        Page<Record> paged = recordService.listByCurrentUser(pagination, getFilters(filter));
        return new PageImpl<>(recordMapper.entitiesToDTOs(paged.getContent()), paged.getPageable(), paged.getContent().size());
    }
}
