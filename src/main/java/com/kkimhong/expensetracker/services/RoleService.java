package com.kkimhong.expensetracker.services;

import com.kkimhong.expensetracker.dtos.request.RoleRequest;
import com.kkimhong.expensetracker.dtos.response.RoleResponse;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    RoleResponse createRole(RoleRequest request);
    RoleResponse getRoleById(UUID id);
    List<RoleResponse> getAllRoles();
    RoleResponse updateRole(UUID id, RoleRequest request);
    void deleteRole(UUID id);
}
