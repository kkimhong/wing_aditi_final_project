package com.kkimhong.expensetracker.mapper;

import com.kkimhong.expensetracker.dtos.request.ExpenseRequest;
import com.kkimhong.expensetracker.dtos.response.ExpenseResponse;
import com.kkimhong.expensetracker.entities.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "submitter",   ignore = true)  // set in service
    @Mapping(target = "department",  ignore = true)  // set in service
    @Mapping(target = "category",    ignore = true)  // resolved from categoryId in service
    @Mapping(target = "status",      ignore = true)  // defaults to DRAFT
    @Mapping(target = "approvedBy",  ignore = true)
    @Mapping(target = "approvedAt",  ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    Expense toEntity(ExpenseRequest request);

    @Mapping(target = "category",      source = "category.name")
    @Mapping(target = "categoryId",    source = "category.id")
    @Mapping(target = "submittedBy",   expression = "java(expense.getSubmitter().getFirstname() + ' ' + expense.getSubmitter().getLastname())")
    @Mapping(target = "submitterId",   source = "submitter.id")
    @Mapping(target = "departmentName",source = "department.name")
    @Mapping(target = "approvedBy",    expression = "java(expense.getApprovedBy() != null ? expense.getApprovedBy().getFirstname() + ' ' + expense.getApprovedBy().getLastname() : null)")
    @Mapping(target = "status",        expression = "java(expense.getStatus().name())")
    ExpenseResponse toResponse(Expense expense);

    List<ExpenseResponse> toResponseList(List<Expense> expenses);
}