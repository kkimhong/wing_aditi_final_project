package com.kkimhong.expensetracker.mapper;

import com.kkimhong.expensetracker.dtos.request.RoleRequest;
import com.kkimhong.expensetracker.dtos.response.PermissionResponse;
import com.kkimhong.expensetracker.dtos.response.RoleResponse;
import com.kkimhong.expensetracker.entities.Role;
import com.kkimhong.expensetracker.entities.RolePermission;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", expression = "java(mapPermissions(role.getRolePermissions()))")
    RoleResponse toResponse(Role role);

    List<RoleResponse> toResponseList(List<Role> roles);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "priority", source = "priority")
    Role toEntity(RoleRequest request);

    default List<PermissionResponse> mapPermissions(Set<RolePermission> rolePermissions) {
        if (rolePermissions == null) return List.of();
        return rolePermissions.stream()
                .map(rp -> new PermissionResponse(
                        rp.getPermission().getId(),
                        rp.getPermission().getModule(),
                        rp.getPermission().getAction(),
                        rp.getPermission().toKey()
                ))
                .toList();
    }
}
