package com.kkimhong.expensetracker.controllers;

import com.kkimhong.expensetracker.dtos.request.RoleRequest;
import com.kkimhong.expensetracker.dtos.response.RoleResponse;
import com.kkimhong.expensetracker.services.RoleService;
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
@RequestMapping(RoleController.BASE_URL)
@RequiredArgsConstructor
@Validated
public class RoleController {

    public static final String BASE_URL = "/api/v1/roles";
    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('roles:create')")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.createRole(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:read')")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('roles:read')")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:update')")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable UUID id,
                                                   @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:delete')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
