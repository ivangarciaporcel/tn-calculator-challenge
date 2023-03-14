package com.tncalculator.calculatorapi.controllers;

import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.dto.UserPartialDTO;
import com.tncalculator.calculatorapi.domain.mapper.UserMapper;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tncalculator.calculatorapi.constants.AuditConstants.CREATED_AT;
import static com.tncalculator.calculatorapi.constants.AuditConstants.UPDATED_AT;
import static com.tncalculator.calculatorapi.constants.MessageConstants.CURRENT_USER_NOT_FOUND;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.domain.model.User.FIELD_USERNAME;
import static com.tncalculator.calculatorapi.security.SecurityUtils.getAuthUserDetails;
import static com.tncalculator.calculatorapi.utils.PageUtils.getFilters;
import static com.tncalculator.calculatorapi.utils.PageUtils.getSortOrders;

@RestController
@RequestMapping(path = "/users")
@Tag(name = "Users", description = "Rest API that provides user CRUD capabilities")
public class UserController extends BaseController<User, UserDTO, UserPartialDTO> {

    private final UserService userService;

    private final UserMapper mapper;

    @Autowired
    public UserController(UserService userService, UserMapper mapper) {
        super(userService, mapper);
        this.userService = userService;
        this.mapper = mapper;
    }

    @Operation(summary = "Create a User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)
    })
    @RolesAllowed(USER_ADMIN)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public UserDTO create(@Parameter(description = "Representation of the user information to be created")
                          @RequestBody @Valid UserDTO dto) {
        return super.create(dto);
    }

    @Operation(summary = "Get a User given an id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @GetMapping("/{id}")
    @Override
    public UserDTO get(@Parameter(description = "Id of the User to be searched")
                       @PathVariable Optional<UUID> id) {
        return super.get(id);
    }

    @Operation(summary = "Update a User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was updated", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
    })
    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @PutMapping("/{id}")
    @Override
    public UserDTO update(@Parameter(description = "Id of the User to be updated") @PathVariable Optional<UUID> id,
                          @Parameter(description = "Representation of the user data to be updated") @RequestBody @Valid UserDTO dto) {
        return super.update(id, dto);
    }

    @Operation(summary = "Patch a User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was patched", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
    })
    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @PatchMapping("/{id}")
    @Override
    public UserDTO patch(@Parameter(description = "Id of the User to be patched") @PathVariable Optional<UUID> id,
                         @Parameter(description = "Representation of the user data to be patched") @RequestBody @Valid UserPartialDTO partialDTO) {
        return super.patch(id, partialDTO);
    }

    @Operation(summary = "Delete a User given an id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
    })
    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @DeleteMapping("/{id}")
    @Override
    public void delete(@Parameter(description = "Id of the User to be deleted") @PathVariable Optional<UUID> id) {
        super.delete(id);
    }

    @Operation(summary = "List users with sorting and filter capabilities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listed users that match the search criteria", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Page.class)))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
    })
    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @GetMapping
    @Override
    public Page<UserDTO> list(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @Parameter(description = "Sort fields separated by a comma, i.e. sort=username,asc&sort=createdAt,desc. Accepted sort fields: username, createdAt and updatedAt")
                              @RequestParam(defaultValue = "username,asc") String[] sort,
                              @Parameter(description = "Filter fields separated by a comma, i.e. filter=email,admin&filter=status,ACTIVE. Accepted filter fields: email and status (ACTIVE, INACTIVE)")
                              @RequestParam(defaultValue = "") String[] filter) {
        List<Sort.Order> orders = getSortOrders(sort, List.of(FIELD_USERNAME, CREATED_AT, UPDATED_AT));
        Pageable pagination = PageRequest.of(page, size, Sort.by(orders));
        Page<User> paged = userService.list(pagination, getFilters(filter));
        return new PageImpl<>(mapper.entitiesToDTOs(paged.getContent()), paged.getPageable(), paged.getContent().size());
    }

    @Operation(summary = "Get logged user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found", content = {@
                    Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @SneakyThrows
    @GetMapping("/current")
    public UserDTO getCurrentUser() {
        UserDetails userDetails = getAuthUserDetails();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(CURRENT_USER_NOT_FOUND, new Object[]{}));
        return mapper.entityToDTO(user);
    }
}
