package com.tncalculator.calculatorapi.domain.mapper;

import org.mapstruct.*;

import java.util.List;

@MapperConfig(uses = CommonMapper.class)
public interface BaseMapper<E, T, P> {

    T entityToDTO(E entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    E dtoToEntity(T dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    void update(@MappingTarget E entity, E updateEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    void patch(P partialDTO, @MappingTarget E entity);

    List<T> entitiesToDTOs(List<E> entities);

    List<E> dtosToEntities(List<T> dtos);
}
