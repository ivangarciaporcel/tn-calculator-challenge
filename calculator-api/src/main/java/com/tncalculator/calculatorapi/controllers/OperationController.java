package com.tncalculator.calculatorapi.controllers;

import com.tncalculator.calculatorapi.domain.dto.CalculatorOperationsDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationPartialDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationResultDTO;
import com.tncalculator.calculatorapi.domain.mapper.OperationMapper;
import com.tncalculator.calculatorapi.domain.model.Operation;
import com.tncalculator.calculatorapi.domain.model.OperationResponse;
import com.tncalculator.calculatorapi.services.OperationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import static com.tncalculator.calculatorapi.constants.MessageConstants.USER_BALANCE_NOT_ENOUGH_OPERATION;
import static com.tncalculator.calculatorapi.domain.model.Operation.FIELD_TYPE;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.utils.PageUtils.getFilters;
import static com.tncalculator.calculatorapi.utils.PageUtils.getSortOrders;

@RestController
@RequestMapping(path = "/operations")
@Tag(name = "Operations", description = "Rest API that provides operation CRUD capabilities")
public class OperationController extends BaseController<Operation, OperationDTO, OperationPartialDTO> {

    private final OperationService operationService;

    private final OperationMapper mapper;

    @Autowired
    public OperationController(OperationService operationService, OperationMapper mapper) {
        super(operationService, mapper);
        this.operationService = operationService;
        this.mapper = mapper;
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Create an Operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operation created", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = OperationDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public OperationDTO create(@Parameter(description = "Representation of the operation information to be created")
                               @RequestBody @Valid OperationDTO dto) {
        return super.create(dto);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get an Operation given an id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation was found", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = OperationDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @SneakyThrows
    @GetMapping("/{id}")
    @Override
    public OperationDTO get(@Parameter(description = "Id of the Operation to be searched")
                            @PathVariable Optional<UUID> id) {
        return super.get(id);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Update an Operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation was updated", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = OperationDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
    })
    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @PutMapping("/{id}")
    @Override
    public OperationDTO update(@Parameter(description = "Id of the Operation to be updated") @PathVariable Optional<UUID> id,
                               @Parameter(description = "Representation of the operation data to be updated") @RequestBody @Valid OperationDTO dto) {
        return super.update(id, dto);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Patch an Operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation was patched", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = OperationDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
    })
    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @PatchMapping("/{id}")
    @Override
    public OperationDTO patch(@Parameter(description = "Id of the Operation to be patched") @PathVariable Optional<UUID> id,
                              @Parameter(description = "Representation of the operation data to be patched") @RequestBody @Valid OperationPartialDTO partialDTO) {
        return super.patch(id, partialDTO);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Delete an Operation given an id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation was deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
    })
    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @DeleteMapping("/{id}")
    @Override
    public void delete(@Parameter(description = "Id of the Operation to be deleted") @PathVariable Optional<UUID> id) {
        super.delete(id);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "List operations with sorting and filter capabilities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listed operations that match the search criteria", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Page.class)))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
    })
    @SneakyThrows
    @GetMapping
    @Override
    public Page<OperationDTO> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @Parameter(description = "Sort fields separated by a comma, i.e. sort=type,asc&sort=createdAt,desc. Accepted sort fields: type, createdAt and updatedAt")
                                   @RequestParam(defaultValue = "type,asc") String[] sort,
                                   @Parameter(description = "Filter fields separated by a comma, i.e. filter=type,ADDITION&filter=status,APPROVED. Accepted filter fields: type and status (IN_VERIFICATION, APPROVED, DEPRECATED)")
                                   @RequestParam(defaultValue = "") String[] filter) {
        List<Sort.Order> orders = getSortOrders(sort, List.of(FIELD_TYPE, CREATED_AT, UPDATED_AT));
        Pageable pagination = PageRequest.of(page, size, Sort.by(orders));
        Page<Operation> paged = operationService.list(pagination, getFilters(filter));
        return new PageImpl<>(mapper.entitiesToDTOs(paged.getContent()), paged.getPageable(), paged.getContent().size());
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Perform calculation of a given operation, for example perform an addition or a multiplication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successfully performed", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = OperationResultDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)
    })
    @SneakyThrows
    @PostMapping("/{id}/calculate")
    public OperationResultDTO calculateOperation(
            @Parameter(description = "Id of the operation to be performed") @PathVariable Optional<UUID> id,
            @Parameter(description = "Parameters specific to each operation, i.e.:" +
                    "\n- ADDITION: parameters are first_number and second_number," +
                    "\n- SUBTRACTION: parameters are minuend and subtrahend," +
                    "\n- MULTIPLICATION: parameters are first_number and second_number," +
                    "\n- DIVISION: parameters are numerator and denominator," +
                    "\n- SQUARE_ROOT: the unique parameter is single_parameter," +
                    "\n- RANDOM_STRING: does not need any parameter"
            )
            @RequestBody @Valid CalculatorOperationsDTO dto) {
        checkArgument(id.isPresent(), ID_NOT_NULL);
        OperationResultDTO result = operationService.calculate(id.get(), dto.getParameters());
        if (OperationResponse.DENIED.equals(result.getOperationResponse())) {
            throw new IllegalArgumentException(USER_BALANCE_NOT_ENOUGH_OPERATION);
        }
        return result;
    }
}
