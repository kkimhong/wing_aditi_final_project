package com.kkimhong.expensetracker.mapper;

import com.kkimhong.expensetracker.dtos.request.DepartmentRequest;
import com.kkimhong.expensetracker.dtos.response.DepartmentDetailResponse;
import com.kkimhong.expensetracker.dtos.response.DepartmentResponse;
import com.kkimhong.expensetracker.entities.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "users",     ignore = true)
    @Mapping(target = "expenses",  ignore = true)
    Department toEntity(DepartmentRequest request);

    @Mapping(target = "userCount",
            expression = "java(department.getUsers().size())")
    DepartmentResponse toResponse(Department department);

    @Mapping(target = "userCount",
            expression = "java(department.getUsers().size())")
    @Mapping(target = "expenseCount",
            expression = "java(department.getExpenses().size())")
    DepartmentDetailResponse toDetailResponse(Department department);

    List<DepartmentResponse> toResponseList(List<Department> departments);
}
