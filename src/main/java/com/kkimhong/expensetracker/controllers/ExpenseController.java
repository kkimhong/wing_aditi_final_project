package com.kkimhong.expensetracker.controllers;

import com.kkimhong.expensetracker.dtos.request.ExpenseRequest;
import com.kkimhong.expensetracker.dtos.request.RejectRequest;
import com.kkimhong.expensetracker.dtos.response.ExpenseResponse;
import com.kkimhong.expensetracker.entities.User;
import com.kkimhong.expensetracker.enums.ExpenseStatus;
import com.kkimhong.expensetracker.services.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(ExpenseController.BASE_URL)
@RequiredArgsConstructor
public class ExpenseController {

    public static final String BASE_URL = "/api/v1/expenses";

    private final ExpenseService expenseService;

    @PostMapping
    @PreAuthorize("hasAuthority('expenses:create')")
    public ResponseEntity<ExpenseResponse> create(
            @RequestBody @Valid ExpenseRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseService.createExpense(request, currentUser));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('expenses:read_own')")
    public ResponseEntity<List<ExpenseResponse>> getMyExpenses(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.getMyExpenses(currentUser));
    }

    // Admin/Auditor — all expenses with optional filters
    @GetMapping
    @PreAuthorize("hasAuthority('expenses:read_all')")
    public ResponseEntity<List<ExpenseResponse>> getAll(
            @RequestParam(required = false) ExpenseStatus status,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
                expenseService.getAllExpenses(status, departmentId, categoryId, startDate, endDate)
        );
    }

    // Manager — expenses scoped to their authorized departments
    @GetMapping("/department")
    @PreAuthorize("hasAuthority('expenses:read_all')")
    public ResponseEntity<List<ExpenseResponse>> getDepartmentExpenses(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.getDepartmentExpenses(currentUser));
    }

    // Manager — pending approvals scoped to their authorized departments
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('expenses:approve')")
    public ResponseEntity<List<ExpenseResponse>> getPending(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.getPendingApprovals(currentUser));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('expenses:read_own')")
    public ResponseEntity<ExpenseResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('expenses:create')")
    public ResponseEntity<ExpenseResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid ExpenseRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.updateExpense(id, request, currentUser));
    }

    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('expenses:create')")
    public ResponseEntity<ExpenseResponse> submit(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.submitExpense(id, currentUser));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('expenses:approve')")
    public ResponseEntity<ExpenseResponse> approve(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.approveExpense(id, currentUser));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('expenses:reject')")
    public ResponseEntity<ExpenseResponse> reject(
            @PathVariable UUID id,
            @RequestBody @Valid RejectRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                expenseService.rejectExpense(id, request.comment(), currentUser)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('expenses:create')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        expenseService.deleteExpense(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}