package com.kkimhong.expensetracker.services.servicesImpl;

import com.kkimhong.expensetracker.configs.StorageService;
import com.kkimhong.expensetracker.dtos.request.ExpenseRequest;
import com.kkimhong.expensetracker.dtos.response.ExpenseResponse;
import com.kkimhong.expensetracker.entities.*;
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
import java.util.Objects;
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

//    @Override
//    public ExpenseResponse createExpense(ExpenseRequest request, User principalUser) {
//        Category category = categoryRepository.findById(request.categoryId())
//                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
//
//        if (category.getLimitPerSubmission() != null &&
//                request.amount().compareTo(category.getLimitPerSubmission()) > 0) {
//            throw new IllegalArgumentException(
//                    "Amount exceeds category limit of " + category.getLimitPerSubmission()
//            );
//        }
//
//        Expense expense = expenseMapper.toEntity(request);
//        expense.setSubmitter(principalUser);
//        expense.setDepartment(principalUser.getDepartment()); // null for Admin — valid
//        expense.setCategory(category);
//        expense.setCurrency(request.currency() != null ? request.currency() : "USD");
//
//        return expenseMapper.toResponse(expenseRepository.save(expense));
//    }

    @Override
    public ExpenseResponse createExpense(ExpenseRequest request, User principalUser) {
        User currentUser = userRepository.findById(principalUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (category.getLimitPerSubmission() != null &&
                request.amount().compareTo(category.getLimitPerSubmission()) > 0) {
            throw new IllegalArgumentException(
                    "Amount exceeds category limit of " + category.getLimitPerSubmission()
            );
        }

        Expense expense = expenseMapper.toEntity(request);
        expense.setSubmitter(currentUser);
        expense.setDepartment(currentUser.getDepartment());
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
        List<UUID> authorizedDeptIds = resolveAuthorizedDepartmentIds(currentUser);

        // Company-wide role (e.g. Admin) — return everything
        if (authorizedDeptIds == null) {
            return expenseMapper.toResponseList(expenseRepository.findAllWithDetails());
        }

        if (authorizedDeptIds.isEmpty()) {
            throw new AccessDeniedException("You have no department scope assigned");
        }

        return expenseMapper.toResponseList(
                expenseRepository.findByDepartmentIdsWithDetails(authorizedDeptIds)
        );
    }

    @Override
    public List<ExpenseResponse> getPendingApprovals(User currentUser) {
        List<UUID> authorizedDeptIds = resolveAuthorizedDepartmentIds(currentUser);

        // Company-wide role (e.g. Admin) — return all pending
        if (authorizedDeptIds == null) {
            return expenseMapper.toResponseList(expenseRepository.findAllPending());
        }

        if (authorizedDeptIds.isEmpty()) {
            throw new AccessDeniedException("You have no department scope assigned");
        }

        return expenseMapper.toResponseList(
                expenseRepository.findPendingByDepartments(authorizedDeptIds)
        );
    }

    @Override
    public ExpenseResponse updateExpense(UUID id, ExpenseRequest request, User currentUser) {
        Expense expense = findOrThrow(id);

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

        if (expense.isOwnedBy(currentUser.getId())) {
            throw new AccessDeniedException("You cannot approve your own expense");
        }

        assertAuthorizedForExpenseDepartment(currentUser, expense);

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
            throw new AccessDeniedException("You cannot reject your own expense");
        }

        assertAuthorizedForExpenseDepartment(currentUser, expense);

        expense.reject(currentUser);
        expense.setNotes(comment);
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

    // ─── Private helpers ──────────────────────────────────────────────────────────

    private Expense findOrThrow(UUID id) {
        return expenseRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
    }

    /**
     * Asserts the current user has an active role scoped to the expense's department.
     * Throws AccessDeniedException if not authorized.
     *
     * A null department on UserRole means company-wide — always passes (e.g. Admin).
     */
    private void assertAuthorizedForExpenseDepartment(User currentUser, Expense expense) {
        UUID expenseDeptId = expense.getDepartment().getId();

        boolean authorized = currentUser.getUserRoles().stream()
                .filter(UserRole::isActive)
                .anyMatch(ur -> ur.isScopedToDepartment(expenseDeptId));

        if (!authorized) {
            throw new AccessDeniedException(
                    "You are not authorized to act on expenses in this department"
            );
        }
    }

    /**
     * Resolves the list of department IDs the current user is authorized for,
     * based on their active UserRole scopes.
     *
     * Returns null  → user has a company-wide role (UserRole.department = null)
     * Returns list  → user is scoped to these specific department IDs only
     * Returns empty → user has no active role with any department scope (deny)
     */
    private List<UUID> resolveAuthorizedDepartmentIds(User currentUser) {
        List<UserRole> activeRoles = currentUser.getUserRoles().stream()
                .filter(UserRole::isActive)
                .toList();

        // Any company-wide role → null signals "no restriction"
        boolean isCompanyWide = activeRoles.stream()
                .anyMatch(ur -> ur.getDepartment() == null);

        if (isCompanyWide) {
            return null;
        }

        return activeRoles.stream()
                .map(UserRole::getDepartment)
                .filter(Objects::nonNull)
                .map(Department::getId)
                .distinct()
                .toList();
    }
}