package com.kkimhong.expensetracker.mapper;

import com.kkimhong.expensetracker.dtos.request.RoleRequest;
import com.kkimhong.expensetracker.dtos.response.RoleResponse;
import com.kkimhong.expensetracker.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponse toResponse(Role role);

    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "rolePermissions", ignore = true)
    @Mapping(target = "userRoles",       ignore = true)
    Role toEntity(RoleRequest request);

    List<RoleResponse> toResponseList(List<Role> roles);
}
