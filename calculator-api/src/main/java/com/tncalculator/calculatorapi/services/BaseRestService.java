package com.tncalculator.calculatorapi.services;

import com.tncalculator.calculatorapi.domain.mapper.BaseMapper;
import com.tncalculator.calculatorapi.domain.model.BaseEntity;
import com.tncalculator.calculatorapi.exceptions.ForbiddenServiceException;
import com.tncalculator.calculatorapi.exceptions.IllegalArgumentServiceException;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.repository.BaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;

public abstract class BaseRestService<E extends BaseEntity, T, P> implements RestService<E, P, UUID> {

    private final BaseRepository<E, UUID> repository;
    private final BaseMapper<E, T, P> mapper;
    private final Class<E> entityClass;

    protected BaseRestService(BaseRepository<E, UUID> repository, BaseMapper<E, T, P> mapper, Class<E> entityClass) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityClass = entityClass;
    }

    @Transactional
    public E create(E entity) {
        validateCreate(entity);
        return repository.save(entity);
    }

    public E findById(UUID id) throws NotFoundException {
        return getById(id);
    }

    public E update(UUID id, E entity) throws NotFoundException, IllegalArgumentServiceException, ForbiddenServiceException {
        E existentEntity = getById(id);
        validateUpdate(entity, existentEntity);
        mapper.update(existentEntity, entity);
        return repository.save(existentEntity);
    }

    public E patch(UUID id, P partial) throws NotFoundException, IllegalArgumentServiceException, ForbiddenServiceException {
        E entity = getById(id);
        validatePatch(entity, partial);
        mapper.patch(partial, entity);
        return repository.save(entity);
    }

    public void delete(UUID id) throws NotFoundException, ForbiddenServiceException {
        E entity = getById(id);
        validateDelete(entity);
        entity.markAsDeleted();
        repository.save(entity);
    }

    public Page<E> list(Pageable pageable, Map<String, String> filters) {
        return repository.listNotDeleted(pageable);
    }

    protected E getById(UUID id) throws NotFoundException {
        checkArgument(id != null, ID_NOT_NULL);
        return repository.findByIdNotDeleted(id).orElseThrow(
                () -> new NotFoundException(ID_NOT_FOUND, new Object[]{entityClass.getSimpleName(), id.toString()}));
    }

    protected abstract void validateCreate(E e);

    protected abstract void validateUpdate(E e, E existentEntity);

    protected abstract void validatePatch(E existentEntity, P partial);

    protected abstract void validateDelete(E e);

    protected Map<String, String> parseFilters(List<String> filters) {
        Map<String, String> filterMap = new HashMap<>();
        filters.forEach(filter -> {
            String[] pair = filter.split(",");
            checkArgument(pair.length == 2, INVALID_FILTER_FORMAT);
            filterMap.put(pair[0], pair[1]);
        });
        return filterMap;
    }
}
