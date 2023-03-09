package com.tncalculator.calculatorapi.domain.mapper;

import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.dto.UserPartialDTO;
import com.tncalculator.calculatorapi.domain.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = BaseMapper.class, uses = {RoleMapper.class})
public interface UserMapper extends BaseMapper<User, UserDTO, UserPartialDTO> {


    @Mapping(source = "username", target = "email")
    @Mapping(source = "userStatus", target = "status")
    @Mapping(source = "authorities", target = "roles")
    UserDTO entityToDTO(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(source = "email", target = "username")
    @Mapping(source = "status", target = "userStatus")
    User dtoToEntity(UserDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void update(@MappingTarget User entity, User updateEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(source = "status", target = "userStatus")
    void patch(UserPartialDTO partialDTO, @MappingTarget User entity);
}
