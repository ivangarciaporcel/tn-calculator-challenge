package com.tncalculator.calculatorapi.domain.mapper;

import com.tncalculator.calculatorapi.domain.dto.RecordDTO;
import com.tncalculator.calculatorapi.domain.model.Record;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, OperationMapper.class})
public interface RecordMapper {

    @Mapping(source = "audit.createdAt", target = "createdAt")
    RecordDTO map(Record record);

    List<RecordDTO> entitiesToDTOs(List<Record> records);
}
