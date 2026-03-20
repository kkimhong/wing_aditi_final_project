package com.kkimhong.expensetracker.mapper;

import com.kkimhong.expensetracker.dtos.request.RegisterRequest;
import com.kkimhong.expensetracker.dtos.response.UserResponse;
import com.kkimhong.expensetracker.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "firstname", source = "firstname")
    @Mapping(target = "lastname",  source = "lastname")
    User toEntity(RegisterRequest request);

    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "permissions", expression = "java(user.getPermissionKeys())")
    UserResponse toResponse(User user);
}
