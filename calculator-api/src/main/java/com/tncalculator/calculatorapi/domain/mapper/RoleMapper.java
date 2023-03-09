package com.tncalculator.calculatorapi.domain.mapper;

import com.tncalculator.calculatorapi.domain.model.Authority;
import com.tncalculator.calculatorapi.security.Roles;
import org.mapstruct.Mapper;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    default List<Roles> map(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(ga -> (Authority) ga).map(Authority::getRole).collect(Collectors.toList());
    }
}
