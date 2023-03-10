package com.tncalculator.calculatorapi.domain.mapper;

import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.dto.UserPartialDTO;
import com.tncalculator.calculatorapi.domain.model.Role;
import com.tncalculator.calculatorapi.domain.model.User;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring", config = BaseMapper.class)
public interface UserMapper extends BaseMapper<User, UserDTO, UserPartialDTO> {


    @Mapping(source = "username", target = "email")
    @Mapping(source = "userStatus", target = "status")
    @Mapping(source = "authorities", target = "roles", qualifiedByName = "roleToString")
    UserDTO entityToDTO(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(source = "roles", target = "authorities", qualifiedByName = "stringToRole")
    @Mapping(source = "email", target = "username")
    @Mapping(source = "status", target = "userStatus")
    User dtoToEntity(UserDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    void update(@MappingTarget User entity, User updateEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(source = "roles", target = "authorities", qualifiedByName = "stringToRole")
    @Mapping(source = "status", target = "userStatus")
    void patch(UserPartialDTO partialDTO, @MappingTarget User entity);

    @Named("stringToRole")
    default Set<Role> stringToRole(Set<String> authorities) {
        if (authorities != null) {
            return authorities.stream().map(Role::new).collect(toSet());
        }
        return new HashSet<>();
    }

    @Named("roleToString")
    default Set<String> roleToString(Set<Role> authorities) {
        if (authorities != null) {
            return authorities.stream().map(Role::getAuthority).collect(toSet());
        }
        return new HashSet<>();
    }
}
