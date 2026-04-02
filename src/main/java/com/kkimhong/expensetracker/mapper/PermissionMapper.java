package com.kkimhong.expensetracker.mapper;


import com.kkimhong.expensetracker.dtos.response.PermissionResponse;
import com.kkimhong.expensetracker.entities.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "key", expression = "java(permission.toKey())")
    PermissionResponse toResponse(Permission permission);

    List<PermissionResponse> toResponseList(List<Permission> permissions);
}
