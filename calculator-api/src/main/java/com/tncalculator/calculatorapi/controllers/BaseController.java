package com.tncalculator.calculatorapi.controllers;

import com.tncalculator.calculatorapi.domain.mapper.BaseMapper;
import com.tncalculator.calculatorapi.services.RestService;
import lombok.SneakyThrows;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.MessageConstants.ID_NOT_NULL;
import static com.tncalculator.calculatorapi.utils.PageUtils.AUDIT_SORT_FIELDS;
import static com.tncalculator.calculatorapi.utils.PageUtils.getSortOrders;

public abstract class BaseController<E, T, P> implements ControllerSpecification<T, P> {

    private final RestService<E, P, UUID> baseService;
    private final BaseMapper<E, T, P> mapper;

    protected BaseController(RestService<E, P, UUID> baseService, BaseMapper<E, T, P> mapper) {
        this.baseService = baseService;
        this.mapper = mapper;
    }

    public T create(T dto) {
        E entity = mapper.dtoToEntity(dto);
        return mapper.entityToDTO(baseService.create(entity));
    }

    @SneakyThrows
    public T get(Optional<UUID> id) {
        checkArgument(id.isPresent(), ID_NOT_NULL);
        return mapper.entityToDTO(baseService.findById(id.get()));
    }

    @SneakyThrows
    public T update(Optional<UUID> id, T t) {
        checkArgument(id.isPresent(), ID_NOT_NULL);
        E e = mapper.dtoToEntity(t);
        return mapper.entityToDTO(baseService.update(id.get(), e));
    }

    @SneakyThrows
    public T patch(Optional<UUID> id, P p) {
        checkArgument(id.isPresent(), ID_NOT_NULL);
        return mapper.entityToDTO(baseService.patch(id.get(), p));
    }

    @SneakyThrows
    public void delete(Optional<UUID> id) {
        checkArgument(id.isPresent(), ID_NOT_NULL);
        baseService.delete(id.get());
    }

    @SneakyThrows
    public Page<T> list(int page, int size, String[] sort) {
        List<Sort.Order> orders = getSortOrders(sort, AUDIT_SORT_FIELDS);
        Pageable pagination = PageRequest.of(page, size, Sort.by(orders));
        Page<E> paged = baseService.list(pagination);
        return new PageImpl<>(mapper.entitiesToDTOs(paged.getContent()), paged.getPageable(), paged.getContent().size());
    }
}
