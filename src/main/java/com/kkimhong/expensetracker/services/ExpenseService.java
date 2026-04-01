package com.kkimhong.expensetracker.services;

import com.kkimhong.expensetracker.dtos.request.ExpenseRequest;
import com.kkimhong.expensetracker.dtos.response.ExpenseResponse;
import com.kkimhong.expensetracker.entities.User;
import com.kkimhong.expensetracker.enums.ExpenseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseRequest request, User currentUser);
    ExpenseResponse getExpenseById(UUID id);
    List<ExpenseResponse> getMyExpenses(User currentUser);
    List<ExpenseResponse> getAllExpenses(ExpenseStatus status, UUID departmentId, UUID categoryId, LocalDate startDate, LocalDate endDate);
    List<ExpenseResponse> getDepartmentExpenses(User currentUser);
    List<ExpenseResponse> getPendingApprovals(User currentUser);
    ExpenseResponse updateExpense(UUID id, ExpenseRequest request, User currentUser);
    ExpenseResponse submitExpense(UUID id, User currentUser);
    ExpenseResponse approveExpense(UUID id, User currentUser);
    ExpenseResponse rejectExpense(UUID id, String comment, User currentUser);
    void deleteExpense(UUID id, User currentUser);
}
