package com.kkimhong.expensetracker.controllers;

import com.kkimhong.expensetracker.dtos.response.PermissionResponse;
import com.kkimhong.expensetracker.repositories.PermissionRepository;
import com.kkimhong.expensetracker.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(PermissionController.BASE_URL)
@RequiredArgsConstructor
public class PermissionController {
    public static final String BASE_URL = "/api/v1/permissions";
    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('permissions:read')")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }
}
