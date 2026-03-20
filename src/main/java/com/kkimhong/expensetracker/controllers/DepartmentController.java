package com.kkimhong.expensetracker.controllers;

import com.kkimhong.expensetracker.dtos.request.DepartmentRequest;
import com.kkimhong.expensetracker.dtos.response.DepartmentResponse;
import com.kkimhong.expensetracker.services.DepartmentService;
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
@RequestMapping(DepartmentController.BASE_URL)
@RequiredArgsConstructor
@Validated
public class DepartmentController {
    public static final String BASE_URL = "/api/v1/departments" ;
    private final DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('departments:read')")
    public ResponseEntity<List<DepartmentResponse>> getAll() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('departments:read')")
    public ResponseEntity<DepartmentResponse> getById(UUID id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('departments:create')")
    public ResponseEntity<DepartmentResponse> create(
            @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departmentService.createDepartment(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('departments:update')")
    public ResponseEntity<DepartmentResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid DepartmentRequest request){
        return ResponseEntity.ok(departmentService.updateDepartment(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('departments:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

}
