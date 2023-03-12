package com.tncalculator.calculatorapi.controllers;

import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.dto.UserPartialDTO;
import com.tncalculator.calculatorapi.domain.mapper.UserMapper;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.services.UserService;
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
import static com.tncalculator.calculatorapi.utils.PageUtils.getSortOrders;

@RestController
@RequestMapping(path = "/users")
public class UserController extends BaseController<User, UserDTO, UserPartialDTO> {

    private final UserService userService;

    private final UserMapper mapper;

    @Autowired
    public UserController(UserService userService, UserMapper mapper) {
        super(userService, mapper);
        this.userService = userService;
        this.mapper = mapper;
    }

    @RolesAllowed(USER_ADMIN)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public UserDTO create(@RequestBody @Valid UserDTO dto) {
        return super.create(dto);
    }

    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @GetMapping("/{id}")
    @Override
    public UserDTO get(@PathVariable Optional<UUID> id) {
        return super.get(id);
    }

    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @PutMapping("/{id}")
    @Override
    public UserDTO update(@PathVariable Optional<UUID> id, @RequestBody @Valid UserDTO dto) {
        return super.update(id, dto);
    }

    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @PatchMapping("/{id}")
    @Override
    public UserDTO patch(@PathVariable Optional<UUID> id, @RequestBody @Valid UserPartialDTO partialDTO) {
        return super.patch(id, partialDTO);
    }

    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Optional<UUID> id) {
        super.delete(id);
    }

    @RolesAllowed(USER_ADMIN)
    @SneakyThrows
    @GetMapping
    @Override
    public Page<UserDTO> list(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(defaultValue = "username,asc") String[] sort) {
        List<Sort.Order> orders = getSortOrders(sort, List.of(FIELD_USERNAME, CREATED_AT, UPDATED_AT));
        Pageable pagination = PageRequest.of(page, size, Sort.by(orders));
        Page<User> paged = userService.list(pagination);
        return new PageImpl<>(mapper.entitiesToDTOs(paged.getContent()), paged.getPageable(), paged.getContent().size());
    }

    @SneakyThrows
    @GetMapping("/current")
    public UserDTO getCurrentUser() {
        UserDetails userDetails = getAuthUserDetails();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(CURRENT_USER_NOT_FOUND, new Object[]{}));
        return mapper.entityToDTO(user);
    }
}
