package com.kkimhong.expensetracker.controllers;

import com.kkimhong.expensetracker.dtos.request.CategoryRequest;
import com.kkimhong.expensetracker.dtos.response.CategoryResponse;
import com.kkimhong.expensetracker.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('categories:read')")
    public ResponseEntity<List<CategoryResponse>> getActive() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('categories:read')")
    public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('categories:create')")
    public ResponseEntity<CategoryResponse> create(
            @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('categories:update')")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('categories:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('categories:update')")
    public ResponseEntity<CategoryResponse> toggle(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.toggleActive(id));
    }
}