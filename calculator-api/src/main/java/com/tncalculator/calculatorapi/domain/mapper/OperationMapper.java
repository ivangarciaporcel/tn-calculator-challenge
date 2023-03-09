package com.tncalculator.calculatorapi.domain.mapper;

import com.tncalculator.calculatorapi.domain.dto.OperationDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationPartialDTO;
import com.tncalculator.calculatorapi.domain.model.Operation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = BaseMapper.class)
public interface OperationMapper extends BaseMapper<Operation, OperationDTO, OperationPartialDTO> {
}
