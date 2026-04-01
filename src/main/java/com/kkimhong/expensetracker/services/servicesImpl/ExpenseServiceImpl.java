package com.kkimhong.expensetracker.services.servicesImpl;

import com.kkimhong.expensetracker.configs.StorageService;
import com.kkimhong.expensetracker.dtos.request.ExpenseRequest;
import com.kkimhong.expensetracker.dtos.response.ExpenseResponse;
import com.kkimhong.expensetracker.entities.Category;
import com.kkimhong.expensetracker.entities.Expense;
import com.kkimhong.expensetracker.entities.User;
import com.kkimhong.expensetracker.enums.ExpenseStatus;
import com.kkimhong.expensetracker.mapper.ExpenseMapper;
import com.kkimhong.expensetracker.repositories.CategoryRepository;
import com.kkimhong.expensetracker.repositories.ExpenseRepository;
import com.kkimhong.expensetracker.repositories.UserRepository;
import com.kkimhong.expensetracker.services.ExpenseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    private final ExpenseMapper expenseMapper;

    @Override
    public ExpenseResponse createExpense(ExpenseRequest request, User principalUser) {
        // Fetch the "managed" user from the DB
        User currentUser = userRepository.findById(principalUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        // Check category limit
        if (category.getLimitPerSubmission() != null &&
                request.amount().compareTo(category.getLimitPerSubmission()) > 0) {
            throw new IllegalArgumentException(
                    "Amount exceeds category limit of " + category.getLimitPerSubmission()
            );
        }

        Expense expense = expenseMapper.toEntity(request);
        expense.setSubmitter(currentUser);
        expense.setDepartment(currentUser.getDepartment()); // ✅ safe now
        expense.setCategory(category);
        expense.setCurrency(request.currency() != null ? request.currency() : "USD");

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override
    public ExpenseResponse getExpenseById(UUID id) {
        return expenseMapper.toResponse(findOrThrow(id));
    }

    @Override
    public List<ExpenseResponse> getMyExpenses(User currentUser) {
        return expenseMapper.toResponseList(
                expenseRepository.findBySubmitterIdWithDetails(currentUser.getId())
        );
    }

    @Override
    public List<ExpenseResponse> getAllExpenses(
            ExpenseStatus status, UUID departmentId,
            UUID categoryId, LocalDate startDate, LocalDate endDate) {
        return expenseMapper.toResponseList(
                expenseRepository.findAllFiltered(status, departmentId, categoryId, startDate, endDate)
        );
    }

    @Override
    public List<ExpenseResponse> getDepartmentExpenses(User currentUser) {
        if (currentUser.getDepartment() == null) {
            throw new IllegalArgumentException("User has no department assigned");
        }
        return expenseMapper.toResponseList(
                expenseRepository.findByDepartmentIdWithDetails(currentUser.getDepartment().getId())
        );
    }

    @Override
    public List<ExpenseResponse> getPendingApprovals(User currentUser) {
        if (currentUser.getDepartment() == null) {
            throw new IllegalArgumentException("User has no department assigned");
        }
        return expenseMapper.toResponseList(
                expenseRepository.findPendingByDepartment(currentUser.getDepartment().getId())
        );
    }

    @Override
    public ExpenseResponse updateExpense(UUID id, ExpenseRequest request, User currentUser) {
        Expense expense = findOrThrow(id);

        // Only owner can edit and only DRAFT expenses
        if (!expense.isOwnedBy(currentUser.getId())) {
            throw new AccessDeniedException("You can only edit your own expenses");
        }
        if (expense.getStatus() != ExpenseStatus.DRAFT) {
            throw new IllegalArgumentException("Only DRAFT expenses can be edited");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        expense.setTitle(request.title());
        expense.setAmount(request.amount());
        expense.setCurrency(request.currency() != null ? request.currency() : "USD");
        expense.setCategory(category);
        expense.setExpenseDate(request.expenseDate());
        expense.setNotes(request.notes());
        expense.setReceiptUrl(request.receiptUrl());

        return expenseMapper.toResponse(expense);
    }

    @Override
    public ExpenseResponse submitExpense(UUID id, User currentUser) {
        Expense expense = findOrThrow(id);

        if (!expense.isOwnedBy(currentUser.getId())) {
            throw new AccessDeniedException("You can only submit your own expenses");
        }
        if (expense.getStatus() != ExpenseStatus.DRAFT) {
            throw new IllegalArgumentException("Only DRAFT expenses can be submitted");
        }

        expense.setStatus(ExpenseStatus.SUBMITTED);
        return expenseMapper.toResponse(expense);
    }

    @Override
    public ExpenseResponse approveExpense(UUID id, User currentUser) {
        Expense expense = findOrThrow(id);

        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new IllegalArgumentException("Only SUBMITTED expenses can be approved");
        }

        boolean canApproveOwn = currentUser.getRole("expenses", "approve_own");

        if (!canApproveOwn && expense.isOwnedBy(currentUser.getId())) {
            throw new IllegalArgumentException("You cannot approve your own expense");
        }

        expense.approve(currentUser);
        return expenseMapper.toResponse(expense);
    }

    @Override
    public ExpenseResponse rejectExpense(UUID id, String comment, User currentUser) {
        Expense expense = findOrThrow(id);

        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new IllegalArgumentException("Only SUBMITTED expenses can be rejected");
        }
        if (expense.isOwnedBy(currentUser.getId())) {
            throw new IllegalArgumentException("You cannot reject your own expense");
        }

        expense.reject(currentUser);
        expense.setNotes(comment);  // store rejection reason in notes
        return expenseMapper.toResponse(expense);
    }

    @Override
    public void deleteExpense(UUID id, User currentUser) {
        Expense expense = findOrThrow(id);

        if (!expense.isOwnedBy(currentUser.getId())) {
            throw new AccessDeniedException("You can only delete your own expenses");
        }
        if (expense.getStatus() != ExpenseStatus.DRAFT) {
            throw new IllegalArgumentException("Only DRAFT expenses can be deleted");
        }

        if (expense.getReceiptUrl() != null) {
            storageService.deleteFile(expense.getReceiptUrl());
        }

        expenseRepository.deleteById(id);
    }

    private Expense findOrThrow(UUID id) {
        return expenseRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
    }
}
