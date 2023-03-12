package com.tncalculator.calculatorapi.domain.mapper;

import com.tncalculator.calculatorapi.domain.dto.OperationDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationPartialDTO;
import com.tncalculator.calculatorapi.domain.model.Operation;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = BaseMapper.class)
public interface OperationMapper extends BaseMapper<Operation, OperationDTO, OperationPartialDTO> {

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "type", ignore = true)
    void patch(OperationPartialDTO partialDTO, @MappingTarget Operation entity);
}
