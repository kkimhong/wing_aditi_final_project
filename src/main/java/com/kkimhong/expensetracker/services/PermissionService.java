package com.kkimhong.expensetracker.services;

import com.kkimhong.expensetracker.dtos.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    List<PermissionResponse> getAllPermissions();
}
